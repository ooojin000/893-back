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
        summary = "회원 기본 정보 조회",
        description = "로그인한 사용자의 이메일, 이름, 프로필 이미지, 소셜 로그인 제공자 정보를 반환합니다."
)
@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
public @interface GetUserInfoApi {
}
