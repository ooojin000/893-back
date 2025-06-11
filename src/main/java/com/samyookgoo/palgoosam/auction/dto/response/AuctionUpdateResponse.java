package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "경매 상품 수정 응답 DTO")
public class AuctionUpdateResponse {

    @Schema(description = "경매 ID", example = "123")
    private Long auctionId;

    @Schema(description = "수정된 경매 제목", example = "아이폰 13 판매합니다")
    private String title;

    @Schema(description = "수정된 상품 설명", example = "충전기 포함, 화면 깨짐 없음")
    private String description;

    @Schema(description = "수정된 시작 가격", example = "150000")
    private Integer basePrice;

    @Schema(description = "상품 상태", example = "GENTLY_USED",
            allowableValues = {"BRAND_NEW", "LIKE_NEW", "GENTLY_USED", "HEAVILY_USED", "DAMAGED"})
    private ItemCondition itemCondition;

    @Schema(description = "경매 시작까지 대기 시간 (분)", example = "10")
    private Integer startDelay;

    @Schema(description = "경매 진행 시간 (분)", example = "60")
    private Integer durationTime;

    @Schema(description = "카테고리 정보")
    private CategoryResponse category;

    @Schema(description = "이미지 목록")
    private List<AuctionImageResponse> images;
}

