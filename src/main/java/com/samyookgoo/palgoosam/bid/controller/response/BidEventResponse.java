package com.samyookgoo.palgoosam.bid.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidEventResponse {
    private Integer currentPrice;
    private Integer totalBid;
    private Integer totalBidder;
    private Boolean isCancelled;
    private BidResponse bid;
}
