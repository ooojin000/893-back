package com.samyookgoo.palgoosam.bid.domain;

public interface BidForHighestPriceProjection {
    Long getAuctionId();

    Integer getBidHighestPrice();
}
