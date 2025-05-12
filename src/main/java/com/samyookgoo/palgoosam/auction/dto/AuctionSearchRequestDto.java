package com.samyookgoo.palgoosam.auction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuctionSearchRequestDto {

    private String keyword;

    // 상품 사용감
    private Boolean isBrandNew;
    private Boolean isLikeNew;
    private Boolean isGentlyUsed;
    private Boolean isHeavilyUsed;
    private Boolean isDamaged;

    private Integer priceMin;
    private Integer priceMax;

    // 경매 진행 상태
    private Boolean isPending;
    private Boolean isActive;
    private Boolean isCompleted;

    @NotNull
    private String sortBy = "latest";

    private Integer mainCategoryId;
    private Integer subCategoryId;
    private Integer detailCategoryId;

    private Integer page = 1;
    private Integer limit = 10;

    public AuctionSearchParam toAuctionSearchParam() {
        Integer categoryId = this.checkCategoryId();

        return new AuctionSearchParam(keyword, categoryId, isBrandNew, isLikeNew, isGentlyUsed, isHeavilyUsed,
                isDamaged, priceMin, priceMax, isPending, isActive, isCompleted, page, limit, sortBy);
    }

    private Integer checkCategoryId() {
        if (this.detailCategoryId != null) {
            return this.detailCategoryId;
        }

        if (this.subCategoryId != null) {
            return this.subCategoryId;
        }

        if (this.mainCategoryId != null) {
            return this.mainCategoryId;
        }

        return null;
    }
}
