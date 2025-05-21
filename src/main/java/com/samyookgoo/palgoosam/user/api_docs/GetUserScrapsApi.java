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
        summary = "회원 스크랩 목록 조회",
        description = "사용자가 스크랩한(찜한) 경매 목록을 조회합니다."
)
@ApiResponse(responseCode = "200", description = "스크랩 경매 조회 성공")
public @interface GetUserScrapsApi {
}
