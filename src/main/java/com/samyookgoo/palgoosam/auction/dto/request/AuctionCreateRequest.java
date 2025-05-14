package com.samyookgoo.palgoosam.auction.dto.request;

import com.samyookgoo.palgoosam.auction.domain.ItemCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Min(0)
    private Integer basePrice;

    @NotNull
    private ItemCondition itemCondition;

    @NotNull
    @Min(0)
    @Max(1440)
    private Integer startDelay;

    @NotNull
    @Min(10)
    @Max(1440)
    private Integer durationTime;

    @NotNull
    private CategoryRequest category;
}