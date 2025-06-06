package com.samyookgoo.palgoosam.payment.controller.response;

import com.samyookgoo.palgoosam.payment.domain.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCreateResponse {
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;
    private String customerMobilePhone;
    private Integer finalPrice;

    public static PaymentCreateResponse from(Payment payment, String orderName, String successUrl, String failUrl) {
        return PaymentCreateResponse.builder()
                .orderId(payment.getOrderNumber())
                .orderName(orderName)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .customerEmail(payment.getRecipientEmail())
                .customerName(payment.getRecipientName())
                .customerMobilePhone(payment.getPhoneNumber())
                .finalPrice(payment.getFinalPrice())
                .build();
    }
}
