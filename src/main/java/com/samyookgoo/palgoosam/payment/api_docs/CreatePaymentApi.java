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
        summary = "결제 요청 생성",
        description = "낙찰된 경매에 대해 결제를 요청합니다. 사용자는 요청 정보를 바탕으로 결제 페이지로 이동하게 됩니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 요청 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 오류 또는 유효하지 않은 상태")
})
public @interface CreatePaymentApi {
}
