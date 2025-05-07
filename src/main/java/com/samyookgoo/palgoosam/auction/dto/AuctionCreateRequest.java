package com.samyookgoo.palgoosam.auction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {
    private String title;
    private String description;
    private int basePrice;
    private itemCondition itemCondition;
    private int startDelay;
    private int durationTime;
    private Long categoryId;
}