package com.samyookgoo.palgoosam.auction.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionUpdateRequest {
    private String title;
    private String description;
    private Integer basePrice;
    private ItemCondition itemCondition;
    private Integer startDelay;
    private Integer durationTime;
    private Long categoryId;
    private List<String> imageUrls;
}
