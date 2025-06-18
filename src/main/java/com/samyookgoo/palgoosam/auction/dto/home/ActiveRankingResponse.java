package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.projection.RankingAuction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActiveRankingResponse {
    private Long auctionId;
    private String title;
    private String description;
    private ItemCondition itemCondition;
    private String thumbnailUrl;
    private Integer bidCount;
    private Integer rankNum;

    public static ActiveRankingResponse from(RankingAuction r, int bidCount, int rankNum) {
        return ActiveRankingResponse.builder()
                .auctionId(r.getAuctionId())
                .title(r.getTitle())
                .description(r.getDescription())
                .itemCondition(r.getItemCondition())
                .thumbnailUrl(r.getThumbnailUrl())
                .bidCount(bidCount)
                .rankNum(rankNum)
                .build();
    }
}
