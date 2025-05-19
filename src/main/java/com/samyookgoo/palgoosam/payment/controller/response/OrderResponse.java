package com.samyookgoo.palgoosam.payment.controller.response;

import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.payment.domain.PaymentStatus;
import com.samyookgoo.palgoosam.payment.domain.PaymentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    private String orderId;
    private Long auctionId;
    private String auctionTitle;
    private String auctionThumbnail;
    private Integer finalPrice;
    private DeliveryAddressResponseDto deliveryAddress;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
}
