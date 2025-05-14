package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.ItemCondition;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionCreateResponse {
    private String title;
    private String description;
    private Integer basePrice;
    private ItemCondition itemCondition;
    private Integer startDelay;
    private Integer durationTime;

    private CategoryResponse category;

    private List<AuctionImageResponse> images;
}
