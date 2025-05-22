package com.samyookgoo.palgoosam.auth.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰이 유효한 경우 새로운 액세스 토큰과 리프레시 토큰을 발급합니다."
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않거나 사용자 조회 실패")
})
public @interface RefreshAccessTokenApi {
}
