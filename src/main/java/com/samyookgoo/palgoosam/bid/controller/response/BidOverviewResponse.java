package com.samyookgoo.palgoosam.bid.controller.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidOverviewResponse {
    private Long auctionId;
    private Integer currentPrice;
    private Integer totalBid;
    private Integer totalBidder;
    private Boolean canCancelBid;
    private BidResponse recentUserBid;
    private List<BidResponse> bids;
    private List<BidResponse> cancelledBids;
}
