package com.samyookgoo.palgoosam.bid.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class BidNotFoundException extends ApiException {
    public BidNotFoundException() {
        super(ErrorCode.BID_NOT_FOUND);
    }
}
