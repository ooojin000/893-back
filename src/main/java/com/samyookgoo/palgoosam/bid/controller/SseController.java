package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.api_docs.SseAuctionSubscribeApi;
import com.samyookgoo.palgoosam.bid.api_docs.SseUserSubscribeApi;
import com.samyookgoo.palgoosam.bid.service.SseService;
import com.samyookgoo.palgoosam.user.domain.User;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final AuthService authService;

    @SseAuctionSubscribeApi
    @CrossOrigin
    @GetMapping(value = "/{auctionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Parameter(name = "auctionId", description = "실시간 알림을 구독할 경매 ID", required = true)
            @PathVariable Long auctionId
    ) {
        return sseService.subscribe(auctionId);
    }

    @SseUserSubscribeApi
    @CrossOrigin
    @GetMapping(value = "/user/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribePersonal() {
        User currentUser = authService.getAuthorizedUser(authService.getCurrentUser());
        return sseService.subscribePersonal(currentUser.getId());
    }
}