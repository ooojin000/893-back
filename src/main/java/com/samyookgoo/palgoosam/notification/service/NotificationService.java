package com.samyookgoo.palgoosam.notification.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.constant.NotificationStatusType;
import com.samyookgoo.palgoosam.notification.domain.NotificationHistory;
import com.samyookgoo.palgoosam.notification.domain.NotificationStatus;
import com.samyookgoo.palgoosam.notification.fcm.dto.FcmMessageData;
import com.samyookgoo.palgoosam.notification.fcm.dto.FcmNotificationRequestDto;
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
    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository auctionImageRepository;
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

    public void saveAndSendFcmMessage(FcmMessageData messageData) {
        NotificationHistory created = saveFcmMessage(messageData);
        Auction auction = auctionRepository.findById(created.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        AuctionImage auctionImage = auctionImageRepository.findMainImageByAuctionId(created.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("AuctionImage not found"));
        FcmNotificationRequestDto requestDto = FcmNotificationRequestDto.builder()
                .auctionId(created.getAuctionId())
                .notificationId(created.getId())
                .auctionTitle(auction.getTitle())
                .message(messageData.getMessage())
                .createdAt(created.getCreatedAt())
//                .messageType(created.getMessageType())
                .imageUrl(auctionImage.getUrl())
                .build();
        log.info("Sending FCM notification request: {}", requestDto.toString());
        fcmService.sendMessage(requestDto);
    }

    private NotificationHistory saveFcmMessage(FcmMessageData messageData) {
        NotificationHistory notification = new NotificationHistory();
        notification.setAuctionId(messageData.getAuctionId());
//        notification.setMessageType(messageData.getMessageType());
        return notificationHistoryRepository.save(notification);
    }

    public void subscribe(Long auctionId, SubscriptionType subscriptionType) {
        auctionSubscriptionService.subscribe(auctionId, subscriptionType);
    }

    public void unsubscribe(Long auctionId, SubscriptionType subscriptionType) {
        auctionSubscriptionService.unsubscribe(auctionId, subscriptionType);
    }

    public void sendTopicMessage(Long auctionId, SubscriptionType subscriptionType, String message,
                                 NotificationStatusType notificationStatusType, String title) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User dummyUser = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setAuctionId(auctionId);
        notificationHistory.setTitle(title);
        notificationHistory.setMessage(message);
        NotificationHistory savedNotification = notificationHistoryRepository.save(notificationHistory);

        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setNotificationHistoryId(savedNotification.getId());
        notificationStatus.setUserId(dummyUser.getId());
        notificationStatus.setNotificationStatusType(notificationStatusType);
        notificationStatusRepository.save(notificationStatus);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        fcmService.sendTopicMessage(auctionId, "Auction_" + auction.getId(), subscriptionType, message, title);
        notificationStatus.setNotificationStatusType(NotificationStatusType.SUCCESS);
        notificationStatusRepository.save(notificationStatus);

    }
}
