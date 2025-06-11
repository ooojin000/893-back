package com.samyookgoo.palgoosam.notification.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.api_docs.DeleteNotificationApi;
import com.samyookgoo.palgoosam.notification.api_docs.GetNotificationListApi;
import com.samyookgoo.palgoosam.notification.api_docs.ReadNotificationApi;
import com.samyookgoo.palgoosam.notification.api_docs.SaveFcmTokenApi;
import com.samyookgoo.palgoosam.notification.dto.NotificationResponseDto;
import com.samyookgoo.palgoosam.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "알림", description = "앱 푸시 및 알림 내역 관리 API")
public class NotificationController {

    private final NotificationService notificationService;

    @SaveFcmTokenApi
    @PostMapping("/fcm-token")
    public ResponseEntity<BaseResponse> saveFcmToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "FCM 토큰 (단일 문자열)", required = true)
            @RequestBody String fcmToken
    ) {
        BaseResponse response = notificationService.saveFcmToken(fcmToken);
        if (response.getCode() == 200) {
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetNotificationListApi
    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationResponseDto>>> getNotificationList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림 내역이 정상적으로 전송되었습니다.", notificationService.getNotificationList()));
    }

    @ReadNotificationApi
    @PatchMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> readNotification(
            @Parameter(name = "notificationId", description = "읽음 처리할 알림 ID", required = true)
            @PathVariable Long notificationId
    ) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림이 정상적으로 읽음 처리되었습니다.", null));
    }

    @DeleteNotificationApi
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> deleteNotification(
            @Parameter(name = "notificationId", description = "삭제할 알림 ID", required = true)
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림이 정상적으로 삭제되었습니다.", null));
    }
}
