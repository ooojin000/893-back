package com.samyookgoo.palgoosam.bid.domain;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import java.time.LocalDateTime;

public interface BidForMyPageProjection {
    Long getBidId();

    Boolean getIsWinning();

    Integer getUserPrice();

    String getTitle();

    LocalDateTime getEndTime();

    LocalDateTime getStartTime();

    AuctionStatus getStatus();

    Long getAuctionId();

    String getMainImageUrl();
}
