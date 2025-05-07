package com.samyookgoo.palgoosam.auction.constant;

public enum AuctionStatus {
    PENDING("pending"),
    ACTIVE("active"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String value;

    AuctionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final String PENDING_VALUE = PENDING.getValue();
    public static final String ACTIVE_VALUE = ACTIVE.getValue();
    public static final String COMPLETED_VALUE = COMPLETED.getValue();
    public static final String CANCELED_VALUE = CANCELED.getValue();

}
