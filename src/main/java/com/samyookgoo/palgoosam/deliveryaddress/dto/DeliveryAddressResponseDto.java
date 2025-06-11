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

    public static DeliveryAddressResponseDto of(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressResponseDto(
                deliveryAddress.getId(),
                deliveryAddress.getName(),
                deliveryAddress.getPhoneNumber(),
                deliveryAddress.getAddressLine1(),
                deliveryAddress.getAddressLine2(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getIsDefault()
        );
    }
}
