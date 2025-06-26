package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.projection.RecentAuction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentAuctionResponse {
    private Long auctionId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private AuctionStatus status;
    private Integer basePrice;

    public static RecentAuctionResponse from(RecentAuction r) {
        return RecentAuctionResponse.builder()
                .auctionId(r.getAuctionId())
                .title(r.getTitle())
                .description(r.getDescription())
                .thumbnailUrl(r.getThumbnailUrl())
                .status(r.getStatus())
                .basePrice(r.getBasePrice())
                .build();
    }
}
