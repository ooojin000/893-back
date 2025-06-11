package com.samyookgoo.palgoosam.deliveryaddress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DeliveryAddressRequestDto {
    @NotBlank(message = "수취인 이름을 입력해주세요")
    @Size(max = 10, message = "이름은 최대 10자까지 입력할 수 있습니다")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(
            regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식은 010-1234-5678 등이어야 합니다"
    )
    private String phoneNumber;

    @NotBlank(message = "우편번호를 입력해주세요")
    @Pattern(
            regexp = "^\\d{5}$",
            message = "우편번호는 숫자 5자리여야 합니다"
    )
    private String zipCode;

    @NotBlank(message = "기본 주소를 입력해주세요")
    private String addressLine1;

    private String addressLine2;
    private boolean isDefault;
}
