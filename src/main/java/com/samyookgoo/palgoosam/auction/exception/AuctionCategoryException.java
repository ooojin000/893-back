package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionCategoryException extends ApiException {
    public AuctionCategoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
