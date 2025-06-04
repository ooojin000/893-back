package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.payment.domain.PaymentForMyPageProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserPaymentsResponseDto {
    private Long auctionId;
    private String orderNumber;
    private String mainImageUrl;
    private Integer finalPrice;
    private String title;

    public static UserPaymentsResponseDto of(PaymentForMyPageProjection paymentProjection) {
        return new UserPaymentsResponseDto(
                paymentProjection.getAuctionId(),
                paymentProjection.getOrderNumber(),
                paymentProjection.getMainImageUrl(),
                paymentProjection.getFinalPrice(),
                paymentProjection.getTitle()
        );
    }
}
