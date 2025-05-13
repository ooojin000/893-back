package com.samyookgoo.palgoosam.notification.service;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.dto.FcmTokenSaveRequestDto;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final FirebaseCloudMessageService fcmService;
    private final UserRepository userRepository;

    public BaseResponse saveFcmToken(FcmTokenSaveRequestDto requestDto) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User user = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String fcmToken = requestDto.getFcmToken();

        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.info("FCM token is null or empty");
            return new BaseResponse(404, "토큰이 없습니다.", null);
        }

        if (!fcmService.validateFcmToken(fcmToken)) {
            return new BaseResponse(400, "유효하지 않은 토큰입니다.", null);
        }

        fcmService.saveFcmToken(fcmToken, user);
        return new BaseResponse(200, "FCM 토큰이 정상적으로 저장되었습니다.", null);
    }

}
