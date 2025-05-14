package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.bid.service.SseService;
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
public class SseController {

    private final SseService sseService;

    @CrossOrigin
    @GetMapping(value = "/{auctionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long auctionId) {
        return sseService.subscribe(auctionId);
    }
}