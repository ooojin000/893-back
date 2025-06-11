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
        summary = "결제 승인 처리",
        description = "PG사 결제가 완료된 후, 클라이언트에서 전달한 정보를 바탕으로 최종 승인 및 DB 기록을 처리합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
        @ApiResponse(responseCode = "400", description = "승인 정보가 유효하지 않거나 승인 실패")
})
public @interface ConfirmPaymentApi {
}
