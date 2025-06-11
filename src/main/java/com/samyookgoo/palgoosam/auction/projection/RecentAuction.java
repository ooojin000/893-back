package com.samyookgoo.palgoosam.auction.projection;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;

public interface RecentAuction {
    Long getAuctionId();

    String getTitle();

    String getDescription();

    String getThumbnailUrl();

    AuctionStatus getStatus();

    Integer getBasePrice();
}
