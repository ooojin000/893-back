package com.samyookgoo.palgoosam.auction.domain;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import java.time.LocalDateTime;

public interface AuctionForMyPageProjection {

    Long getAuctionId();

    String getTitle();

    LocalDateTime getEndTime();

    LocalDateTime getStartTime();

    AuctionStatus getStatus();

    String getMainImageUrl();
}
