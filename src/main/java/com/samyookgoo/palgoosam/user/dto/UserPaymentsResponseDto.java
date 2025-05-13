package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.payment.domain.Payment;
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

    public static UserPaymentsResponseDto of(
            Payment payment,
            Long auctionId,
            String mainImageUrl,
            String title
    ) {
        return new UserPaymentsResponseDto(
                auctionId,
                payment.getOrderNumber(),
                mainImageUrl,
                payment.getFinalPrice(),
                title
        );
    }
}
