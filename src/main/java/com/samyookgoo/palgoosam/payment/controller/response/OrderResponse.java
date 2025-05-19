package com.samyookgoo.palgoosam.payment.controller.response;

import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.payment.domain.PaymentMethod;
import com.samyookgoo.palgoosam.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주문 정보 응답 DTO")
public class OrderResponse {

    @Schema(description = "주문 ID (UUID)", example = "a72e8a70-d3bd-4ad6-9232-601f978cd33e")
    private String orderId;

    @Schema(description = "경매 ID", example = "101")
    private Long auctionId;

    @Schema(description = "경매 상품 제목", example = "아이폰 13 미개봉 새제품")
    private String auctionTitle;

    @Schema(description = "경매 상품 썸네일 이미지 URL", example = "https://cdn.893auction.com/images/thumb_101.jpg")
    private String auctionThumbnail;

    @Schema(description = "최종 결제 금액", example = "530000")
    private Integer finalPrice;

    @Schema(description = "배송지 정보")
    private DeliveryAddressResponseDto deliveryAddress;

    @Schema(description = "결제 수단", example = "KAKAO_PAY",
            allowableValues = {"CREDIT_CARD", "NAVER_PAY", "KAKAO_PAY", "PHONE", "BANK_TRANSFER", "NORMAL"})
    private PaymentMethod paymentMethod;

    @Schema(description = "결제 상태", example = "PENDING",
            allowableValues = {"PENDING", "SUCCESS", "FAILED", "CANCELLED"})
    private PaymentStatus paymentStatus;
}
