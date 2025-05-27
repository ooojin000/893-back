package com.samyookgoo.palgoosam.bid.controller.response;

import lombok.Builder;

@Builder
public class BidResultResponse {
    private Boolean canCancelBid;
    private BidResponse bid;

    public static BidResultResponse from(BidResponse bid, boolean canCancelBid) {
        return BidResultResponse.builder()
                .bid(bid)
                .canCancelBid(canCancelBid)
                .build();
    }
}
