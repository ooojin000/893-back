package com.samyookgoo.palgoosam.bid.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class BidForbiddenException extends ApiException {
    public BidForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
