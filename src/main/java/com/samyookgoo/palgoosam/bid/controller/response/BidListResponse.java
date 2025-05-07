package com.samyookgoo.palgoosam.bid.controller.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidListResponse {
    private Long auctionId;
    private int totalBid;
    private int totalBidder;
    private List<BidResponse> bids;
    private List<BidResponse> cancelledBids;
}
