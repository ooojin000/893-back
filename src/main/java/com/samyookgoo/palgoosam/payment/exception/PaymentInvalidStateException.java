package com.samyookgoo.palgoosam.payment.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class PaymentInvalidStateException extends ApiException {
    public PaymentInvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
