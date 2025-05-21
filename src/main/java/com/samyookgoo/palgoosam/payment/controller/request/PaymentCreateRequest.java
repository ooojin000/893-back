package com.samyookgoo.palgoosam.payment.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class PaymentCreateRequest {
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

    @NotNull(message = "경매 상품 금액은 필수입니다.")
    @Min(value = 0, message = "경매 상품 금액은 0원 이상이어야 합니다.")
    private Integer itemPrice;

    @NotNull(message = "배송비는 필수입니다.")
    @Min(value = 0, message = "배송비는 0원 이상이어야 합니다.")
    private Integer deliveryFee;

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
