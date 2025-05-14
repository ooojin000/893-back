package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionImageResponse {
    private Long imageId;
    private String originalName;
    private String storeName;
    private String url;
    private Integer imageSeq;

    public static AuctionImageResponse from(AuctionImage image) {
        return AuctionImageResponse.builder()
                .imageId(image.getId())
                .originalName(image.getOriginalName())
                .storeName(image.getStoreName())
                .url(image.getUrl())
                .imageSeq(image.getImageSeq())
                .build();
    }
}
