package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.projection.RankingAuction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingRankingResponse {
    private Long auctionId;
    private String title;
    private String description;
    private ItemCondition itemCondition;
    private String thumbnailUrl;
    private Integer scrapCount;
    private Integer rankNum;

    public static PendingRankingResponse from(RankingAuction r, int scrapCount, int rankNum) {
        return PendingRankingResponse.builder()
                .auctionId(r.getAuctionId())
                .title(r.getTitle())
                .description(r.getDescription())
                .itemCondition(r.getItemCondition())
                .thumbnailUrl(r.getThumbnailUrl())
                .scrapCount(scrapCount)
                .rankNum(rankNum)
                .build();
    }
}
