package com.samyookgoo.palgoosam.bid.controller.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidListResponse {
    private Long auctionId;
    private Integer totalBid;
    private Integer totalBidder;
    private BidResponse recentUserBid;
    private List<BidResponse> bids;
    private List<BidResponse> cancelledBids;
}
