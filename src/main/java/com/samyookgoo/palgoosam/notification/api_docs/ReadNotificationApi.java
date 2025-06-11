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
        summary = "알림 읽음 처리",
        description = "알림 ID를 기반으로 해당 알림을 읽음 상태로 변경합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
        @ApiResponse(responseCode = "404", description = "알림 ID를 찾을 수 없음")
})
public @interface ReadNotificationApi {
}
