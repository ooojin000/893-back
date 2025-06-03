package com.samyookgoo.palgoosam.search.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class SearchHistoryBadRequestException extends ApiException {
    public SearchHistoryBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
