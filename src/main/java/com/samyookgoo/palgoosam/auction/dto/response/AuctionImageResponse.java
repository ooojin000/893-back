package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuctionImageResponse {
    private String originalName;
    private String storeName;
    private String url;
    private Integer imageSeq;

    public static AuctionImageResponse from(AuctionImage image) {
        return AuctionImageResponse.builder()
                .originalName(image.getOriginalName())
                .storeName(image.getStoreName())
                .url(image.getUrl())
                .imageSeq(image.getImageSeq())
                .build();
    }
}