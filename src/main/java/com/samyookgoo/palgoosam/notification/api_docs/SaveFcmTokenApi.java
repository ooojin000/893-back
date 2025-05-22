package com.samyookgoo.palgoosam.notification.api_docs;

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
        summary = "FCM 토큰 저장",
        description = "클라이언트에서 전달된 FCM 토큰을 저장합니다. 푸시 알림 발송을 위해 필요합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "FCM 토큰 저장 성공"),
        @ApiResponse(responseCode = "400", description = "토큰 저장 실패")
})
public @interface SaveFcmTokenApi {
}
