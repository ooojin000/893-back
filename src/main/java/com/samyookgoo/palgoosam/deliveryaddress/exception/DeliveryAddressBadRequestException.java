package com.samyookgoo.palgoosam.deliveryaddress.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class DeliveryAddressBadRequestException extends ApiException {
    public DeliveryAddressBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
