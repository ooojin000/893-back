package com.samyookgoo.palgoosam.deliveryaddress.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class DeliveryAddressNotFoundException extends ApiException {
    public DeliveryAddressNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
