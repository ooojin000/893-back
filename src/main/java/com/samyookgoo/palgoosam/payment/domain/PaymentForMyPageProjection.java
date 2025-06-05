package com.samyookgoo.palgoosam.payment.domain;

public interface PaymentForMyPageProjection {
    Long getAuctionId();

    String getOrderNumber();

    String getMainImageUrl();

    Integer getFinalPrice();

    String getTitle();
}
