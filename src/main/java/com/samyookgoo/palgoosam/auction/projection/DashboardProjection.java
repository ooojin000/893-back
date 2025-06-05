package com.samyookgoo.palgoosam.auction.projection;

public interface DashboardProjection {
    Long getTotalUserCount();

    Long getTotalAuctionCount();

    Long getActiveAuctionCount();
}
