package com.samyookgoo.palgoosam.auction.projection;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import java.time.LocalDateTime;

public interface UpcomingAuction {
    Long getAuctionId();

    String getTitle();

    String getDescription();

    ItemCondition getItemCondition();

    Integer getBasePrice();

    String getThumbnailUrl();

    LocalDateTime getStartTime();
}
