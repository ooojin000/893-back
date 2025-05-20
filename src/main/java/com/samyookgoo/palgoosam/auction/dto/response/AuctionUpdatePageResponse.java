package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "경매 수정 페이지 응답 DTO")
public class AuctionUpdatePageResponse {

    @Schema(description = "경매 ID", example = "123")
    private Long auctionId;

    @Schema(description = "카테고리 ID", example = "5")
    private Long categoryId;

    @Schema(description = "경매 제목", example = "아이폰 13 중고 팝니다")
    private String title;

    @Schema(description = "상품 설명", example = "생활 기스 있으나 정상 작동합니다.")
    private String description;

    @Schema(description = "상품 상태", example = "GENTLY_USED",
            allowableValues = {"BRAND_NEW", "LIKE_NEW", "GENTLY_USED", "HEAVILY_USED", "DAMAGED"})
    private ItemCondition itemCondition;

    @Schema(description = "경매 상태", example = "PENDING",
            allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private AuctionStatus status;

    @Schema(description = "경매 시작 가격", example = "50000")
    private Integer basePrice;

    @Schema(description = "카테고리 상세 정보")
    private CategoryResponse category;

    @Schema(description = "대표 이미지")
    private AuctionImageResponse mainImage;

    @Schema(description = "전체 이미지 목록")
    private List<AuctionImageResponse> images;
}
