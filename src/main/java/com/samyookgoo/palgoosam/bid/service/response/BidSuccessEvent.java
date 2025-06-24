package com.samyookgoo.palgoosam.bid.service.response;

import com.samyookgoo.palgoosam.bid.domain.Bid;


public record BidSuccessEvent(Long userId, Long auctionId, Bid bid, BidStatsResponse bidStats,
                              Boolean canCancelBid) {
}