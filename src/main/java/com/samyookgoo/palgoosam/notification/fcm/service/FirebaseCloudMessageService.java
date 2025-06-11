package com.samyookgoo.palgoosam.notification.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.TopicManagementResponse;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.notification.fcm.domain.UserFcmToken;
import com.samyookgoo.palgoosam.notification.fcm.dto.FcmMessageDto;
import com.samyookgoo.palgoosam.notification.fcm.repository.UserFcmTokenRepository;
import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseCloudMessageService {
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final AuthService authService;

    public Boolean validateFcmToken(String token) {
        Message message = Message.builder()
                .putData("validationTest", "validationTest")
                .setToken(token)
                .build();
        try {
            FirebaseMessaging.getInstance().send(message, true);
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

    public void sendMessage(FcmMessageDto messageDto) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        List<UserFcmToken> tokenData = userFcmTokenRepository.findUserFcmTokenListByUserId(user.getId());
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(messageDto.getAuctionTitle())
                        .setBody(messageDto.getMessage())
                        .build())
                .putData("subscriptionType", messageDto.getSubscriptionType().toString())
                .putData("imageUrl", messageDto.getImageUrl())
                .putData("auctionId", messageDto.getAuctionId().toString())
                .putData("notificationId", messageDto.getNotificationId().toString())
                .putData("createdAt", messageDto.getCreatedAt().toString())
                .addAllTokens(tokenData.stream().map(UserFcmToken::getToken).collect(Collectors.toList()))
                .build();
        try {
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("{} Message sent successfully.", messageDto.getAuctionTitle());
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribeAuction(String topic, List<String> fcmTokenList) {
        try {
            TopicManagementResponse response = FirebaseMessaging
                    .getInstance()
                    .subscribeToTopic(fcmTokenList, topic);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void unSubscribeAuction(String topic, List<String> fcmTokenList) {
        try {
            TopicManagementResponse response = FirebaseMessaging
                    .getInstance()
                    .unsubscribeFromTopic(fcmTokenList, topic);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTopicMessage(Long auctionId, String topic, SubscriptionType subscriptionType, String message,
                                 String title, String imageUrl) {

        Message fcmMessage = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build()
                )
                .putData("subscriptionType", subscriptionType.toString())
                .putData("imageUrl", imageUrl)
                .putData("auctionId", auctionId.toString())
                .putData("createdAt", LocalDateTime.now().toString())
                .setTopic(topic).build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("메세지 발송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
