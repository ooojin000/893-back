package com.samyookgoo.palgoosam.notification.subscription.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.notification.fcm.domain.UserFcmToken;
import com.samyookgoo.palgoosam.notification.fcm.repository.UserFcmTokenRepository;
import com.samyookgoo.palgoosam.notification.fcm.service.FirebaseCloudMessageService;
import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
import com.samyookgoo.palgoosam.notification.subscription.domain.AuctionSubscription;
import com.samyookgoo.palgoosam.notification.subscription.repository.AuctionSubscriptionRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionSubscriptionService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionSubscriptionRepository auctionSubscriptionRepository;
    private final FirebaseCloudMessageService fcmService;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final AuthService authService;

    public void subscribe(Long auctionId, SubscriptionType subscriptionType) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        List<AuctionSubscription> subscriptionList = auctionSubscriptionRepository.findAllByUserAndAuction(user,
                auction);

        if (subscriptionList.isEmpty()) {
            createSubscription(user, auction, subscriptionType);
            List<String> fcmTokenList = userFcmTokenRepository.findUserFcmTokenListByUserId(user.getId()).stream()
                    .map(UserFcmToken::getToken).collect(Collectors.toList());
            fcmService.subscribeAuction("Auction_" + auction.getId(), fcmTokenList);
        }

        List<AuctionSubscription> targetList = getTargetList(subscriptionList, subscriptionType);

        if (!subscriptionList.isEmpty() && targetList.isEmpty()) {
            createSubscription(user, auction, subscriptionType);
        }
    }

    public void unsubscribe(Long auctionId, SubscriptionType subscriptionType) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        List<AuctionSubscription> subscriptionList = auctionSubscriptionRepository.findAllByUserAndAuction(user,
                auction);

        List<AuctionSubscription> targetList = getTargetList(subscriptionList, subscriptionType);
        auctionSubscriptionRepository.deleteAll(targetList);
        if (subscriptionList.size() == targetList.size()) {
            List<String> fcmTokenList = userFcmTokenRepository.findUserFcmTokenListByUserId(user.getId()).stream()
                    .map(UserFcmToken::getToken).collect(Collectors.toList());
            fcmService.unSubscribeAuction("Auction_" + auction.getId(), fcmTokenList);
        }
    }

    private void createSubscription(User user, Auction auction, SubscriptionType subscriptionType) {
        AuctionSubscription createdSubscription = new AuctionSubscription();
        createdSubscription.setUser(user);
        createdSubscription.setAuction(auction);
        createdSubscription.setType(subscriptionType);
        auctionSubscriptionRepository.save(createdSubscription);
    }

    private List<AuctionSubscription> getTargetList(List<AuctionSubscription> subscriptionList,
                                                    SubscriptionType subscriptionType) {
        return subscriptionList.stream()
                .filter(existing -> existing.getType().equals(subscriptionType)).toList();
    }
}
