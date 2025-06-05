package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionForbiddenException extends ApiException {
    public AuctionForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
