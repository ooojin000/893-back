package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionNotFoundException extends ApiException {
    public AuctionNotFoundException() {
        super(ErrorCode.AUCTION_NOT_FOUND);
    }
}
