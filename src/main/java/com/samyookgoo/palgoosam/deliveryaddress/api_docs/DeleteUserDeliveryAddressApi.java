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
        summary = "회원 배송지 삭제",
        description = "회원의 특정 배송지를 삭제합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "배송지 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "배송지 삭제 실패")
})
public @interface DeleteUserDeliveryAddressApi {
}
