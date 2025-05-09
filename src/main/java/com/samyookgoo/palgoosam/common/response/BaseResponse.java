package com.samyookgoo.palgoosam.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(String message, T body) {
        return new BaseResponse<>(200, message, body);
    }

    public static <T> BaseResponse<T> error(String message, T body) {
        return new BaseResponse<>(400, message, body);
    }
}