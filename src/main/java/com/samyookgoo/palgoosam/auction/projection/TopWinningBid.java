package com.samyookgoo.palgoosam.auction.projection;

public interface TopWinningBid {
    Long getAuctionId();

    String getTitle();

    Integer getBasePrice();

    Integer getItemPrice();

    String getThumbnailUrl();

    String getBuyer();
}
