package com.samyookgoo.palgoosam.common.s3;

import com.samyookgoo.palgoosam.auction.exception.AuctionImageException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.presign-duration-minutes:10}")
    private long presignDurationMinutes;

    public S3PresignedResponse createPresignedUrl(S3PresignedRequest request) {
        try {
            String originalName = request.getFileName();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String storeFileName = UUID.randomUUID() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storeFileName)
                    .build();

            // Presigned URL 생성
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignDurationMinutes)) // yml 60분 유효
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return S3PresignedResponse.builder()
                    .presignedUrl(presignedRequest.url().toString())
                    .storeName(storeFileName)
                    .expirationTime(presignedRequest.expiration())
                    .build();

        } catch (S3Exception e) {
            throw new RuntimeException("S3 URL 생성 중 오류가 발생했습니다.", e);
        }
    }

    public S3PresignedResponse getPresignedUrl(String storeName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(storeName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignDurationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return S3PresignedResponse.builder()
                    .presignedUrl(presignedRequest.url().toString())
                    .storeName(storeName)
                    .expirationTime(presignedRequest.expiration())
                    .build();

        } catch (S3Exception e) {
            throw new RuntimeException("S3 GET URL 생성 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteObject(String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);

            log.info("S3 객체 삭제 성공 - key: {}", key);
        } catch (Exception e) {
            log.error("S3 객체 삭제 실패 - key: {}", key, e);
            throw new AuctionImageException(ErrorCode.AUCTION_IMAGE_DELETE_FAILED);
        }
    }

}
