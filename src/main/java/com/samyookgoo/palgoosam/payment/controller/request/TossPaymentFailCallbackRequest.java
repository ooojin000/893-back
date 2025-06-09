package com.samyookgoo.palgoosam.payment.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TossPaymentFailCallbackRequest {
    @NotBlank(message = "주문 번호는 필수 입니다.")
    private String orderNumber;

    @NotBlank(message = "에러 코드는 필수 입니다.")
    private String errorCode;

    @NotBlank(message = "에러 메시지는 필수 입니다.")
    private String errorMessage;
}
