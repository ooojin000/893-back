package com.samyookgoo.palgoosam.notification.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.dto.NotificationResponseDto;
import com.samyookgoo.palgoosam.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "FCM 토큰 저장",
            description = "클라이언트에서 전달된 FCM 토큰을 저장합니다. 푸시 알림 발송을 위해 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FCM 토큰 저장 성공"),
            @ApiResponse(responseCode = "400", description = "토큰 저장 실패")
    })
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

    @Operation(
            summary = "알림 목록 조회",
            description = "현재 로그인한 사용자의 알림 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationResponseDto>>> getNotificationList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림 내역이 정상적으로 전송되었습니다.", notificationService.getNotificationList()));
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "알림 ID를 기반으로 해당 알림을 읽음 상태로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "알림 ID를 찾을 수 없음")
    })
    @PatchMapping("/{notificationId}")
    public ResponseEntity<BaseResponse<Void>> readNotification(
            @Parameter(name = "notificationId", description = "읽음 처리할 알림 ID", required = true)
            @PathVariable Long notificationId
    ) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(200, "알림이 정상적으로 읽음 처리되었습니다.", null));
    }

    @Operation(
            summary = "알림 삭제",
            description = "알림 ID를 기반으로 해당 알림을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "알림 ID를 찾을 수 없음")
    })
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
