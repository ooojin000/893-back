package com.samyookgoo.palgoosam.user.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "회원 입찰 목록 조회",
        description = "로그인한 사용자가 입찰에 참여한 경매 내역을 조회합니다."
)
@ApiResponse(responseCode = "200", description = "입찰 목록 조회 성공")
public @interface GetUserBidsApi {
}
