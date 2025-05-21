package com.samyookgoo.palgoosam.payment.controller.request;

import com.samyookgoo.palgoosam.payment.constant.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TossPaymentConfirmRequest {
    @NotBlank(message = "페이먼트 키는 필수 값입니다.")
    private String paymentKey;

    @NotBlank(message = "주문 번호는 필수 값입니다.")
    private String orderId;

    @NotNull(message = "결제 금액은 필수 값입니다.")
    private long amount;

    @NotBlank(message = "결제 타입은 필수 값입니다.")
    private PaymentType paymentType;
}
