package com.samyookgoo.palgoosam.auction.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class AuctionUpdateLockedException extends ApiException {
    public AuctionUpdateLockedException() {
        super(ErrorCode.AUCTION_UPDATE_LOCKED);
    }
}
