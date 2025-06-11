package com.samyookgoo.palgoosam.payment.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class PaymentForbiddenException extends ApiException {
    public PaymentForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
