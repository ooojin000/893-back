package com.samyookgoo.palgoosam.payment.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentConfirmResponse {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String approvedAt;
    private Integer totalAmount;
}
