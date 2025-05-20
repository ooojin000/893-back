package com.samyookgoo.palgoosam.payment.controller.response;

import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
    private Long auctionId;
    private String auctionTitle;
    private String auctionThumbnail;
    private Integer itemPrice;
    private Integer deliveryFee;
    private Integer finalPrice;
    private DeliveryAddressResponseDto deliveryAddress;
}
