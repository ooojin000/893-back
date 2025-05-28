package com.samyookgoo.palgoosam.bid.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class BidInvalidStateException extends ApiException {
    public BidInvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
