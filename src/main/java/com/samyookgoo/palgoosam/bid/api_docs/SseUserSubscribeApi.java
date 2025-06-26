package com.samyookgoo.palgoosam.bid.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "입찰 실시간 알림 구독",
        description = "유저 ID를 기준으로 실시간 입찰 알림을 구독합니다. " +
                "SSE 연결을 통해 클라이언트는 실시간으로 이벤트를 수신할 수 있습니다."
)
@ApiResponse(responseCode = "200", description = "SSE 스트림 연결 성공 (text/event-stream)")
public @interface SseUserSubscribeApi {
}
