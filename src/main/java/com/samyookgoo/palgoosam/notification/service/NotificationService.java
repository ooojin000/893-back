package com.samyookgoo.palgoosam.notification.service;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.constant.NotificationStatusType;
import com.samyookgoo.palgoosam.notification.domain.NotificationHistory;
import com.samyookgoo.palgoosam.notification.domain.NotificationStatus;
import com.samyookgoo.palgoosam.notification.dto.NotificationRequestDto;
import com.samyookgoo.palgoosam.notification.fcm.dto.FcmMessageDto;
import com.samyookgoo.palgoosam.notification.fcm.service.FirebaseCloudMessageService;
import com.samyookgoo.palgoosam.notification.repository.NotificationHistoryRepository;
import com.samyookgoo.palgoosam.notification.repository.NotificationStatusRepository;
import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
import com.samyookgoo.palgoosam.notification.subscription.service.AuctionSubscriptionService;
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
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final AuctionSubscriptionService auctionSubscriptionService;
    private final NotificationStatusRepository notificationStatusRepository;

    public BaseResponse saveFcmToken(String fcmToken) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User user = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));

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

    public void saveAndSendFcmMessage(NotificationRequestDto notificationRequestDto) {
        NotificationHistory created = saveFcmMessage(notificationRequestDto);
        FcmMessageDto requestDto = FcmMessageDto.builder()
                .auctionId(created.getAuctionId())
                .notificationId(created.getId())
                .auctionTitle(created.getTitle())
                .message(created.getMessage())
                .createdAt(created.getCreatedAt())
                .subscriptionType(notificationRequestDto.getSubscriptionType())
                .imageUrl(notificationRequestDto.getImageUrl())
                .build();
        log.info("{}: Sending FCM notification request", requestDto.getNotificationId());
        fcmService.sendMessage(requestDto);
    }

    private NotificationHistory saveFcmMessage(NotificationRequestDto notificationRequestDto) {
        NotificationHistory notification = new NotificationHistory();
        notification.setAuctionId(notificationRequestDto.getAuctionId());
        notification.setTitle(notificationRequestDto.getTitle());
        notification.setMessage(notificationRequestDto.getMessage());
        return notificationHistoryRepository.save(notification);
    }

    public void subscribe(Long auctionId, SubscriptionType subscriptionType) {
        auctionSubscriptionService.subscribe(auctionId, subscriptionType);
    }

    public void unsubscribe(Long auctionId, SubscriptionType subscriptionType) {
        auctionSubscriptionService.unsubscribe(auctionId, subscriptionType);
    }

    public void sendTopicMessage(NotificationRequestDto notificationRequestDto) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User dummyUser = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        NotificationHistory createdNotification = saveFcmMessage(notificationRequestDto);

        NotificationStatus notificationStatus = saveNotificationStatus(createdNotification.getId(), dummyUser.getId());

        try {
            // 알림 전송
            fcmService.sendTopicMessage(createdNotification.getAuctionId(),
                    "Auction_" + createdNotification.getAuctionId(), notificationRequestDto.getSubscriptionType(),
                    createdNotification.getMessage(), createdNotification.getTitle());

            // 알림이 정상적으로 전송되면 해당 알림의 상태를 SUCCESS로 변경
            notificationStatus.setNotificationStatusType(NotificationStatusType.SUCCESS);
            notificationStatusRepository.save(notificationStatus);
        } catch (RuntimeException e) {
            // 알림 실패 시 3번 더 호출하는 로직 작성해야 함.
            // 알림이 3번 더 전송해도 실패하면, 실패 상태로 데이터베이스에 저장
            if (notificationStatus.getRetryCount() > 3) {
                notificationStatus.setNotificationStatusType(NotificationStatusType.FAILED);
            }
            notificationStatusRepository.save(notificationStatus);
        }
    }

    private NotificationStatus saveNotificationStatus(Long notificationId, Long userId) {
        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setNotificationHistoryId(notificationId);
        notificationStatus.setUserId(userId);

        // FCM 알림 전송이 실패할 수도 있으니 PENDING 으로 저장
        notificationStatus.setNotificationStatusType(NotificationStatusType.PENDING);
        return notificationStatusRepository.save(notificationStatus);
    }
}
