package com.samyookgoo.palgoosam.deliveryaddress.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "회원 배송지 목록 조회",
        description = "로그인한 회원의 등록된 모든 배송지 목록을 반환합니다."
)
@ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공")
public @interface GetUserDeliveryAddressesApi {
}
