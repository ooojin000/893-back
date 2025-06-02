package com.samyookgoo.palgoosam.user.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class UserUnauthorizedException extends ApiException {
    public UserUnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED_USER);
    }
}
