package com.samyookgoo.palgoosam.payment.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class PaymentExternalException extends ApiException {
    public PaymentExternalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
