package com.samyookgoo.palgoosam.deliveryaddress.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeliveryAddressRequestDto {
    private String name;
    private String phoneNumber;
    private String zipCode;
    private String addressLine1;
    private String addressLine2;
    private boolean isDefault;
}
