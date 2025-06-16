package com.samyookgoo.palgoosam.auction.dto.home;

import com.samyookgoo.palgoosam.auction.projection.TopWinningBid;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopBidResponse {
    private Long auctionId;
    private String title;
    private Integer bidCount;
    private Integer basePrice;
    private Integer itemPrice;
    private String thumbnailUrl;
    private String buyer;
    private Integer rankNum;

    public static TopBidResponse from(TopWinningBid t, String buyer, int bidCount, int rankNum) {
        return TopBidResponse.builder()
                .auctionId(t.getAuctionId())
                .title(t.getTitle())
                .bidCount(bidCount)
                .basePrice(t.getBasePrice())
                .itemPrice(t.getItemPrice())
                .thumbnailUrl(t.getThumbnailUrl())
                .buyer(buyer)
                .rankNum(rankNum)
                .build();
    }
}
