package com.samyookgoo.palgoosam.deliveryaddress.api_docs;

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
        summary = "기본 배송지 설정",
        description = "회원의 배송지 중 하나를 기본 배송지로 설정합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 배송지 설정 성공"),
        @ApiResponse(responseCode = "400", description = "기본 배송지 설정 실패")
})
public @interface PatchUserDefaultAddress {
}
