package com.samyookgoo.palgoosam.payment.controller.request;

import com.samyookgoo.palgoosam.payment.domain.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreatePaymentRequest {
    @NotBlank(message = "수령인 이름은 필수 항목입니다.")
    private String recipientName;

    @NotBlank(message = "연락처는 필수 항목입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "올바른 휴대폰 번호 형식이어야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "배송지는 필수 항목입니다.")
    private String addressLine1;
    private String addressLine2;

    @NotBlank(message = "우편번호는 필수 항목입니다.")
    @Pattern(regexp = "^\\d{5}$", message = "우편번호는 숫자 5자리여야 합니다.")
    private String zipCode;

    @NotNull(message = "결제 수단을 선택해주세요.")
    private PaymentMethod paymentMethod;

    @NotNull(message = "주문번호는 필수입니다.")
    private String orderId;

    @NotNull(message = "페이먼트키는 필수입니다.")
    private String paymentKey;
    
    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0원 이상이어야 합니다.")
    private Integer finalPrice;

    @NotBlank(message = "successUrl은 필수 항목입니다.")
    @URL(message = "유효한 URL이어야 합니다.")
    private String successUrl;

    @NotBlank(message = "failUrl은 필수 항목입니다.")
    @URL(message = "유효한 URL이어야 합니다.")
    private String failUrl;
}
