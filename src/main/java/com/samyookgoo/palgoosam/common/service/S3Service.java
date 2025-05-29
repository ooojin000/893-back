package com.samyookgoo.palgoosam.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.api.client.util.Value;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3에 파일 업로드
    public void uploadFile(String keyName, InputStream inputStream) {
        amazonS3.putObject(bucketName, keyName, inputStream, new ObjectMetadata());
    }

    // S3에서 파일 다운로드
    public S3ObjectInputStream downloadFile(String keyName) {
        S3Object s3Object = amazonS3.getObject(bucketName, keyName);
        return s3Object.getObjectContent();
    }

    // S3에서 파일 삭제
    public void deleteFile(String keyName) {
        amazonS3.deleteObject(bucketName, keyName);
    }
}
