package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionImageException extends ApiException {
    public AuctionImageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
