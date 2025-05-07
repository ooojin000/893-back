package com.samyookgoo.palgoosam.auction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchParam {
    private String keyword;

    private Integer categoryId;

    // 상품 사용감
    private Boolean isBrandNew;
    private Boolean isLikeNew;
    private Boolean isGentlyUsed;
    private Boolean isHeavilyUsed;
    private Boolean isDefective;

    private Integer minPrice;
    private Integer maxPrice;

    private Boolean isPending;
    private Boolean isActive;
    private Boolean isCompleted;

    private Integer page = 1;
    private Integer limit = 10;

    @NotNull
    private String sortBy = "latest";
}
