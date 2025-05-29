package com.samyookgoo.palgoosam.auction.projection;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;

public interface ActiveRanking {
    Long getAuctionId();

    String getTitle();

    String getDescription();

    ItemCondition getItemCondition();

    String getThumbnailUrl();
}
