package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.bid.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "실시간 입찰 알림 (SSE)", description = "경매 입찰 실시간 알림 스트리밍 API")
public class SseController {

    private final SseService sseService;

    @Operation(
            summary = "입찰 실시간 알림 구독",
            description = "경매 ID를 기준으로 해당 경매에 대한 실시간 입찰 알림을 구독합니다. " +
                    "SSE 연결을 통해 클라이언트는 실시간으로 입찰 변동 정보를 수신할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "SSE 스트림 연결 성공 (text/event-stream)")
    @CrossOrigin
    @GetMapping(value = "/{auctionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Parameter(name = "auctionId", description = "실시간 알림을 구독할 경매 ID", required = true)
            @PathVariable Long auctionId
    ) {
        return sseService.subscribe(auctionId);
    }
}