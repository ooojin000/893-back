package com.samyookgoo.palgoosam.auction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "경매 이미지 응답 DTO")
public class AuctionImageResponse {
    private Long imageId;

    private String storeName;

    @Schema(description = "이미지 접근 URL", example = "https://cdn.893.com/images/f3a1c2e3-iphone.jpg")
    private String url;

    @Schema(description = "이미지 순서 (0이 대표 이미지)", example = "0")
    private Integer imageSeq;

    public static AuctionImageResponse from(Long imageId, String storeName, String presignedUrl, int imageSeq) {
        return AuctionImageResponse.builder()
                .imageId(imageId)
                .storeName(storeName)
                .url(presignedUrl)
                .imageSeq(imageSeq)
                .build();
    }
}
