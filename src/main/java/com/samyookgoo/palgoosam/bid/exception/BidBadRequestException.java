package com.samyookgoo.palgoosam.bid.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class BidBadRequestException extends ApiException {
    public BidBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
