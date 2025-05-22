package com.samyookgoo.palgoosam.notification.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "알림 목록 조회",
        description = "현재 로그인한 사용자의 알림 목록을 조회합니다."
)
@ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
public @interface GetNotificationListApi {
}
