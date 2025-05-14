package com.samyookgoo.palgoosam.deliveryaddress.dto;

import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeliveryAddressResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private Boolean isDefault;

    public static DeliveryAddressResponseDto of(DeliveryAddress e) {
        return new DeliveryAddressResponseDto(
                e.getId(),
                e.getName(),
                e.getPhoneNumber(),
                e.getAddressLine1(),
                e.getAddressLine2(),
                e.getZipCode(),
                e.getIsDefault()
        );
    }
}
