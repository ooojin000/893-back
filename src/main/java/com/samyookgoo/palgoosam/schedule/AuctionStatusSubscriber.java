package com.samyookgoo.palgoosam.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.bid.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionStatusSubscriber implements MessageListener {
    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            AuctionStatusPayload payload = objectMapper.readValue(json, AuctionStatusPayload.class);

            Long auctionId = payload.auctionId();
            AuctionStatus status = payload.status();
            AuctionStatusEventResponse event = new AuctionStatusEventResponse(
                    auctionId,
                    status,
                    LocalDateTime.now()
            );

            sseService.broadcastStatusUpdate(auctionId, event);
            log.info("🔔 상태 전환 이벤트 수신 → auctionId={}, status={}", auctionId, status);


        } catch (JsonProcessingException e) {
            log.error("pub/sub 메시지 json 파싱 실패", e);
        }

    }
}
