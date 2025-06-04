package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionInvalidStateException extends ApiException {
    public AuctionInvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
