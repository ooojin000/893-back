package com.samyookgoo.palgoosam.auction.dto;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuctionImageResponse {
    private String imageUrl;

    public static AuctionImageResponse from(AuctionImage image) {
        return AuctionImageResponse.builder()
                .imageUrl("/upload/" + image.getStoreName())
                .build();
    }
}
