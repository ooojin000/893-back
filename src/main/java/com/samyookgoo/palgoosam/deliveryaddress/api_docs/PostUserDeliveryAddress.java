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
        summary = "회원 배송지 등록",
        description = "새로운 배송지를 등록합니다. 기본 배송지 여부도 함께 설정할 수 있습니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "배송지 등록 성공"),
        @ApiResponse(responseCode = "400", description = "배송지 등록 실패")
})
public @interface PostUserDeliveryAddress {
}
