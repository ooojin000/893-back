package com.samyookgoo.palgoosam.payment.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;
    private String customerMobilePhone;
    private Integer finalPrice;
}
