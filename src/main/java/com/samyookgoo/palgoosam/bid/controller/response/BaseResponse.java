package com.samyookgoo.palgoosam.bid.controller.response;

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

    public static <T> BaseResponse<T> success(T body) {
        return new BaseResponse<>(200, "success", body);
    }

    public static <T> BaseResponse<T> error(String message, T body) {
        return new BaseResponse<>(400, message, body);
    }
}
