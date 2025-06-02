package com.samyookgoo.palgoosam.user.exception;

import com.samyookgoo.palgoosam.global.exception.ApiException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;

public class UserForbiddenException extends ApiException {
    public UserForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
