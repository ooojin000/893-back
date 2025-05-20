package com.samyookgoo.palgoosam.auction.service.dto;

import jakarta.validation.constraints.Min;
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
public class AuctionSearchDto {
    private String keyword;

    private Long categoryId;

    // 상품 사용감
    private Boolean isBrandNew;
    private Boolean isLikeNew;
    private Boolean isGentlyUsed;
    private Boolean isHeavilyUsed;
    private Boolean isDamaged;

    private Integer minPrice;
    private Integer maxPrice;

    private Boolean isPending;
    private Boolean isActive;
    private Boolean isCompleted;

    @Min(1)
    @NotNull
    private Integer page = 1;

    @Min(5)
    @NotNull
    private Integer limit = 10;

    @NotNull
    private String sortBy = "latest";
}
