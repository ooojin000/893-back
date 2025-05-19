package com.samyookgoo.palgoosam.payment.controller.request;

import com.samyookgoo.palgoosam.payment.domain.PaymentType;
import lombok.Data;

@Data
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private long amount;
    private PaymentType paymentType;
}
