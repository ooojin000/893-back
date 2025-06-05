package com.samyookgoo.palgoosam.search.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class SearchHistoryNotFoundException extends ApiException {
    public SearchHistoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
