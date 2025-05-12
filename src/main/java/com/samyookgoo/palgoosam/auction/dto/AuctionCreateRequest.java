package com.samyookgoo.palgoosam.auction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {
    private String title;
    private String description;
    private Integer basePrice;
    private ItemCondition itemCondition;
    private Integer startDelay;
    private Integer durationTime;
    private Long categoryId;
}