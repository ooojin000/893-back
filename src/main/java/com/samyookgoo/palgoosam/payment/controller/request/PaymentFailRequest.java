package com.samyookgoo.palgoosam.payment.controller.request;

import lombok.Data;

@Data
public class PaymentFailRequest {
    private String orderNumber;
    private String errorCode;
    private String errorMessage;
}
