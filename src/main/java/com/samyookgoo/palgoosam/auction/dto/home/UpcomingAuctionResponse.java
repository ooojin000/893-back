package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.projection.UpcomingAuction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpcomingAuctionResponse {
    private Long auctionId;
    private String title;
    private String description;
    private ItemCondition itemCondition;
    private Integer basePrice;
    private Integer scrapCount;
    private String thumbnailUrl;
    private String leftTime;

    public static UpcomingAuctionResponse from(UpcomingAuction u, int scrapCount, String leftTime) {
        return UpcomingAuctionResponse.builder()
                .auctionId(u.getAuctionId())
                .title(u.getTitle())
                .description(u.getDescription())
                .itemCondition(u.getItemCondition())
                .basePrice(u.getBasePrice())
                .scrapCount(scrapCount)
                .thumbnailUrl(u.getThumbnailUrl())
                .leftTime(leftTime)
                .build();
    }
}
