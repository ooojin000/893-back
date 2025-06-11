package com.samyookgoo.palgoosam.bid.service.response;

import lombok.Getter;

@Getter
public class BidStatsResponse {
    private final Integer maxPrice;
    private final int totalBid;
    private final int totalBidder;

    public BidStatsResponse(Integer maxPrice, long totalBid, long totalBidder) {
        this.maxPrice = maxPrice;
        this.totalBid = (int) totalBid;
        this.totalBidder = (int) totalBidder;
    }
}