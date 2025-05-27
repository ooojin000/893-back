package com.samyookgoo.palgoosam.payment.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class PaymentBadRequestException extends ApiException {
    public PaymentBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
