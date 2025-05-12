package com.samyookgoo.palgoosam.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.samyookgoo.palgoosam.notification.domain.UserFcmToken;
import com.samyookgoo.palgoosam.notification.repository.UserFcmTokenRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseCloudMessageService {
    private final UserFcmTokenRepository userFcmTokenRepository;

    public Boolean validateFcmToken(String token) {
        Message message = Message.builder()
                .putData("validationTest", "validationTest")
                .setToken(token)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message, true);
            return true;
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode errorCode = e.getMessagingErrorCode();
            if (errorCode.equals(MessagingErrorCode.INTERNAL) || errorCode.equals(MessagingErrorCode.UNAVAILABLE)) {
                log.info("FCM Server Error");
            } else if (errorCode.equals(MessagingErrorCode.UNREGISTERED) || errorCode.equals(
                    MessagingErrorCode.INVALID_ARGUMENT)) {
                log.info("Invalid FCM Token.");

            } else if (errorCode.equals(MessagingErrorCode.THIRD_PARTY_AUTH_ERROR) || errorCode.equals(
                    MessagingErrorCode.SENDER_ID_MISMATCH)) {
                log.info("설정 오류일 수 있습니다. FCM 설정 및 토큰 확인 요망");
            }
            return false;
        }
    }

    public void saveFcmToken(String token, User user) {
        UserFcmToken userFcmToken = UserFcmToken.builder()
                .user(user)
                .token(token)
                .deviceType("WEB")
                .build();
        userFcmTokenRepository.save(userFcmToken);
    }

}
