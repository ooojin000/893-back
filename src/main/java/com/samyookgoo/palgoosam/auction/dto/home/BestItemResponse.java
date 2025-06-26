package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.projection.SubCategoryBestItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BestItemResponse {
    private Long auctionId;
    private String title;
    private AuctionStatus status;
    private ItemCondition itemCondition;
    private String thumbnailUrl;
    private Boolean isAuctionImminent;  // 경매 임박 여부
    private Integer scrapCount;
    private Integer rankNum;

    public static BestItemResponse from(SubCategoryBestItem s, boolean isAuctionImminent, int scrapCount, int rankNum) {
        return BestItemResponse.builder()
                .auctionId(s.getAuctionId())
                .title(s.getTitle())
                .status(s.getStatus())
                .itemCondition(s.getItemCondition())
                .thumbnailUrl(s.getThumbnailUrl())
                .isAuctionImminent(isAuctionImminent)
                .scrapCount(scrapCount)
                .rankNum(rankNum)
                .build();
    }
}
