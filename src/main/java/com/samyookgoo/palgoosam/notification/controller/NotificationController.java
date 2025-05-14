package com.samyookgoo.palgoosam.notification.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/fcm-token")
    public ResponseEntity<BaseResponse> saveFcmToken(@RequestBody String fcmToken) {
        BaseResponse response = notificationService.saveFcmToken(fcmToken);
        if (response.getCode() == 200) {
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }


}
