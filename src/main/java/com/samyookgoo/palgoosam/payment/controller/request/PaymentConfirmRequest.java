package com.samyookgoo.palgoosam.payment.controller.request;

import lombok.Data;

@Data
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private long amount;
}
