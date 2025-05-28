package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "경매 상품 상세 응답 DTO")
public class AuctionDetailResponse {

    @Schema(description = "경매 ID", example = "123")
    private Long auctionId;

    @Schema(description = "경매 제목", example = "아이폰 13 미개봉")
    private String title;

    @Schema(description = "상품 설명", example = "미개봉 새 제품입니다.")
    private String description;

    @Schema(description = "판매자 이메일 마스킹", example = "user****@gmail.com")
    private String sellerEmailMasked;

    @Schema(description = "경매 상태", example = "ACTIVE",
            allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private AuctionStatus status;

    @Schema(description = "상품 상태", example = "BRAND_NEW",
            allowableValues = {"BRAND_NEW", "LIKE_NEW", "GENTLY_USED", "HEAVILY_USED", "DAMAGED"})
    private ItemCondition itemCondition;

    @Schema(description = "경매 시작 가격", example = "100000")
    private Integer basePrice;

    @Schema(description = "해당 유저가 이 경매를 스크랩했는지 여부", example = "true")
    private Boolean isScraped;

    @Schema(description = "스크랩한 유저 수", example = "12")
    private Integer scrapCount;

    @Schema(description = "요청한 사용자가 판매자인지 여부", example = "false")
    private Boolean isSeller;

    @Schema(description = "카테고리 상세 정보")
    private CategoryResponse category;

    @Schema(description = "경매 시작 시각", example = "2025-05-20 14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "경매 종료 시각", example = "2025-05-20 15:00:00")
    private LocalDateTime endTime;

    @Schema(description = "대표 이미지 정보")
    private AuctionImageResponse mainImage;

    @Schema(description = "이미지 목록")
    private List<AuctionImageResponse> images;

    @Schema(description = "현재 사용자가 낙찰자인지 여부", example = "true")
    private boolean isCurrentUserBuyer;

    @Schema(description = "결제가 완료되었는지 여부", example = "false")
    private boolean hasBeenPaid;
}

