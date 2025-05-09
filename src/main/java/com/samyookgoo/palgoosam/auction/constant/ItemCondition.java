package com.samyookgoo.palgoosam.auction.constant;

public enum ItemCondition {
    BRAND_NEW("brand_new"),
    LIKE_NEW("like_new"),
    GENTLY_USED("gently_used"),
    HEAVILY_USED("heavily_used"),
    DAMAGED("damaged");

    private final String value;

    ItemCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final String BRAND_NEW_VALUE = BRAND_NEW.getValue();
    public static final String LIKE_NEW_VALUE = LIKE_NEW.getValue();
    public static final String GENTLY_USED_VALUE = GENTLY_USED.getValue();
    public static final String HEAVILY_USED_VALUE = HEAVILY_USED.getValue();
    public static final String DAMAGED_VALUE = DAMAGED.getValue();
}
