package com.samyookgoo.palgoosam.notification.controller;

import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.dto.NotificationResponseDto;
import com.samyookgoo.palgoosam.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository auctionImageRepository;

    @PostMapping("/fcm-token")
    public ResponseEntity<BaseResponse> saveFcmToken(@RequestBody String fcmToken) {
        BaseResponse response = notificationService.saveFcmToken(fcmToken);
        if (response.getCode() == 200) {
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationResponseDto>>> getNotificationList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림 내역이 정상적으로 전송되었습니다.", notificationService.getNotificationList()));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> readNotification(@PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림이 정상적으로 읽음 처리되었습니다.", null));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림이 정상적으로 삭제되었습니다.", null));
    }
}
