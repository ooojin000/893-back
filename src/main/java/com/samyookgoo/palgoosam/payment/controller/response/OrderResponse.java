package com.samyookgoo.palgoosam.payment.controller.response;

import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주문 정보 응답 DTO")
public class OrderResponse {

    @Schema(description = "경매 ID", example = "101")
    private Long auctionId;

    @Schema(description = "구매자 이름", example = "홍길동")
    private String customerName;

    @Schema(description = "경매 상품 금액", example = "530000")
    private Integer itemPrice;

    @Schema(description = "배송비", example = "0")
    private Integer deliveryFee;

    @Schema(description = "최종 결제 금액", example = "530000")
    private Integer finalPrice;

    @Schema(description = "배송지 정보")
    private DeliveryAddressResponseDto deliveryAddress;
}
