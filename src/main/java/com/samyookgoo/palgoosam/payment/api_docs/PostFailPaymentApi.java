package com.samyookgoo.palgoosam.payment.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "결제 실패 처리",
        description = "결제 도중 실패한 경우 해당 실패 정보를 백엔드에 전달합니다. 이는 로그 또는 상태 업데이트 용도로 사용됩니다."
)
@ApiResponse(responseCode = "200", description = "결제 실패 처리 완료")
public @interface PostFailPaymentApi {
}
