package com.samyookgoo.palgoosam.notification.service;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.notification.constant.NotificationStatusType;
import com.samyookgoo.palgoosam.notification.domain.NotificationHistory;
import com.samyookgoo.palgoosam.notification.domain.NotificationStatus;
import com.samyookgoo.palgoosam.notification.dto.NotificationRequestDto;
import com.samyookgoo.palgoosam.notification.dto.NotificationResponseDto;
import com.samyookgoo.palgoosam.notification.fcm.dto.FcmMessageDto;
import com.samyookgoo.palgoosam.notification.fcm.service.FirebaseCloudMessageService;
import com.samyookgoo.palgoosam.notification.repository.NotificationHistoryRepository;
import com.samyookgoo.palgoosam.notification.repository.NotificationStatusRepository;
import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
import com.samyookgoo.palgoosam.notification.subscription.domain.AuctionSubscription;
import com.samyookgoo.palgoosam.notification.subscription.repository.AuctionSubscriptionRepository;
import com.samyookgoo.palgoosam.notification.subscription.service.AuctionSubscriptionService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
    private final AuctionSubscriptionRepository auctionSubscriptionRepository;
    private final AuctionImageRepository auctionImageRepository;

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

    public void saveAndSendFcmMessage(NotificationRequestDto notificationRequestDto, Long userId) {
        NotificationHistory createdNotification = saveFcmMessage(notificationRequestDto);
        FcmMessageDto requestDto = FcmMessageDto.builder()
                .auctionId(createdNotification.getAuctionId())
                .notificationId(createdNotification.getId())
                .auctionTitle(createdNotification.getTitle())
                .message(createdNotification.getMessage())
                .createdAt(createdNotification.getCreatedAt())
                .subscriptionType(notificationRequestDto.getSubscriptionType())
                .imageUrl(notificationRequestDto.getImageUrl())
                .build();
        log.info("{}: Sending FCM notification request", requestDto.getNotificationId());

        NotificationStatus notificationStatus = saveNotificationStatus(createdNotification.getId(), userId);

        try {
            // 알림 전송
            fcmService.sendMessage(requestDto);

            // 알림이 정상적으로 전송되면 해당 알림의 상태를 SUCCESS로 변경
            notificationStatus.setNotificationStatusType(NotificationStatusType.SUCCESS);
            notificationStatusRepository.save(notificationStatus);
        } catch (RuntimeException e) {
            // TODO: 알림 실패 시 3번 더 호출하는 로직 작성해야 함.
        }
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

    public void sendTopicMessage(NotificationRequestDto notificationRequestDto, List<Long> userIdList) {

        NotificationHistory createdNotification = saveFcmMessage(notificationRequestDto);

        List<NotificationStatus> notificationStatusList = saveNotificationStatusList(createdNotification.getId(),
                userIdList);

        try {
            // 알림 전송
            fcmService.sendTopicMessage(createdNotification.getAuctionId(),
                    "Auction_" + createdNotification.getAuctionId(), notificationRequestDto.getSubscriptionType(),
                    createdNotification.getMessage(), createdNotification.getTitle(),
                    notificationRequestDto.getImageUrl());

            // 알림이 정상적으로 전송되면 해당 알림의 상태를 SUCCESS로 변경
            notificationStatusList.forEach(
                    notificationStatus -> notificationStatus.setNotificationStatusType(NotificationStatusType.SUCCESS));
            notificationStatusRepository.saveAll(notificationStatusList);
        } catch (RuntimeException e) {
            // TODO: 알림 실패 시 3번 더 호출하는 로직 작성해야 함.
            // 알림이 3번 더 전송해도 실패하면, 실패 상태로 데이터베이스에 저장
//            if () {
//                notificationStatusList.forEach(
//                        notificationStatus -> notificationStatus.setNotificationStatusType(
//                                NotificationStatusType.FAILED));
//                notificationStatusRepository.saveAll(notificationStatusList);
//            }
        }
    }

    private NotificationStatus saveNotificationStatus(Long notificationId, Long userId) {
        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setNotificationHistoryId(notificationId);
        notificationStatus.setUserId(userId);
        notificationStatus.setNotificationStatusType(NotificationStatusType.PENDING);

        // FCM 알림 전송이 실패할 수도 있으니 PENDING 으로 저장

        return notificationStatusRepository.save(notificationStatus);
    }

    private List<NotificationStatus> saveNotificationStatusList(Long notificationId, List<Long> userIdList) {
        List<NotificationStatus> notificationStatusList = userIdList.stream().map(userId -> {
            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setNotificationHistoryId(notificationId);
            notificationStatus.setUserId(userId);
            notificationStatus.setNotificationStatusType(NotificationStatusType.PENDING);
            return notificationStatus;
        }).collect(Collectors.toList());

        // FCM 알림 전송이 실패할 수도 있으니 PENDING 으로 저장

        return notificationStatusRepository.saveAll(notificationStatusList);
    }

    public List<NotificationResponseDto> getNotificationList() {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User user = userRepository.findById(4L).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return getUserNotifications(user);
    }

    private List<NotificationResponseDto> getUserNotifications(User user) {

        List<NotificationStatus> notificationStatusList = notificationStatusRepository.findAllByUserIdAndIsDeletedFalse(
                user.getId());

        List<NotificationHistory> notificationHistoryList = notificationHistoryRepository.findAllById(
                notificationStatusList.stream().map(NotificationStatus::getNotificationHistoryId).toList());

        List<AuctionSubscription> auctionSubscriptionList = auctionSubscriptionRepository.findAllByUser(user);

        Map<Long, NotificationHistory> historyMap = notificationHistoryList.stream()
                .collect(Collectors.toMap(NotificationHistory::getId, history -> history));

        Map<Long, List<SubscriptionType>> subscriptionTypesByAuctionId = auctionSubscriptionList.stream()
                .collect(Collectors.groupingBy(
                        sub -> sub.getAuction().getId(),
                        Collectors.mapping(AuctionSubscription::getType, Collectors.toList())
                ));

        return notificationStatusList.stream()
                .map(status -> {

                    // 해당 status에 매칭되는 history 찾기
                    NotificationHistory history = historyMap.get(status.getNotificationHistoryId());

                    if (history == null) {
                        return null;
                    }

                    // 해당 경매의 구독 타입 목록 찾기
                    List<SubscriptionType> subscriptionTypes = subscriptionTypesByAuctionId
                            .getOrDefault(history.getAuctionId(), Collections.emptyList());
                    AuctionImage image = auctionImageRepository.findMainImageByAuctionId(history.getAuctionId())
                            .orElse(null);

                    return NotificationResponseDto.builder()
                            .id(history.getId())
                            .subscriptionTypeList(subscriptionTypes)
                            .title(history.getTitle())
                            .message(history.getMessage())
                            .createdAt(history.getCreatedAt())
                            .auctionId(history.getAuctionId())
                            .isRead(status.getIsRead())
                            .imageUrl(image != null ? image.getUrl() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void readNotification(Long notificationId) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User user = userRepository.findById(4L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        notificationStatusRepository.findByUserIdAndNotificationHistoryId(user.getId(), notificationId)
                .ifPresent((notificationStatus) -> {
                    notificationStatus.setIsRead(true);
                    notificationStatusRepository.save(notificationStatus);
                });
    }

    public void deleteNorification(Long notificationId) {
        /*
        사용자 식별을 위한 로직 추가 (userService)
        현재 유저 판별 코드는 임시로 작성되었습니다.
        * */
        User user = userRepository.findById(4L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        notificationStatusRepository.findByUserIdAndNotificationHistoryId(user.getId(), notificationId)
                .ifPresent((notificationStatus) -> {
                    notificationStatus.setIsDeleted(true);
                    notificationStatusRepository.save(notificationStatus);
                });
    }
}
