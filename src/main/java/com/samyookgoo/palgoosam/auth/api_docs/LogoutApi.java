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
        summary = "로그아웃",
        description = "로그인한 사용자의 토큰을 만료시키고, 관련된 쿠키와 DB 정보를 삭제합니다."
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
})
public @interface LogoutApi {
}
