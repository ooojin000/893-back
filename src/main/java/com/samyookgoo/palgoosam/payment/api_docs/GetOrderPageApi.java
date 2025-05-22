package com.samyookgoo.palgoosam.payment.api_docs;

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
        summary = "주문 정보 조회",
        description = "경매 ID에 해당하는 주문 정보를 조회합니다. 로그인한 사용자 기준으로 낙찰 정보를 반환합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "주문 정보 또는 경매 정보를 찾을 수 없음")
})
public @interface GetOrderPageApi {
}
