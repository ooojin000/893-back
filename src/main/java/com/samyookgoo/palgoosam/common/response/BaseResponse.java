package com.samyookgoo.palgoosam.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API 기본 응답 형식")
public class BaseResponse<T> {

    @Schema(description = "응답 코드")
    private int code;

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "실제 데이터 응답")
    private T data;

    public static <T> BaseResponse<T> success(String message, T body) {
        return new BaseResponse<>(200, message, body);
    }

    public static <T> BaseResponse<T> error(String message, T body) {
        return new BaseResponse<>(400, message, body);
    }
}