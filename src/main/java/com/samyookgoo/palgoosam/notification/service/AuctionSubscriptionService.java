package com.samyookgoo.palgoosam.notification.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.notification.constant.SubscriptionType;
import com.samyookgoo.palgoosam.notification.domain.AuctionSubscription;
import com.samyookgoo.palgoosam.notification.domain.UserFcmToken;
import com.samyookgoo.palgoosam.notification.repository.AuctionSubscriptionRepository;
import com.samyookgoo.palgoosam.notification.repository.UserFcmTokenRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void subscribe(Long auctionId, SubscriptionType subscriptionType) {
        /*
        로그인에 따라 User를 가져오는 로직
         */
        User dummyUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        AuctionSubscription existing = auctionSubscriptionRepository.findByUserAndAuction(dummyUser, auction)
                .orElse(null);

        if (existing == null) {
            AuctionSubscription createdSubscription = new AuctionSubscription();
            createdSubscription.setUser(dummyUser);
            createdSubscription.setAuction(auction);
            createdSubscription.setType(subscriptionType);
            auctionSubscriptionRepository.save(createdSubscription);
            List<String> fcmTokenList = userFcmTokenRepository.findUserFcmTokenListByUserId(dummyUser.getId()).stream()
                    .map(UserFcmToken::getToken).collect(Collectors.toList());
            fcmService.subscribeAuction("Auction_" + auction.getId(), fcmTokenList);
        }
    }

    public void unsubscribe(Long auctionId, SubscriptionType subscriptionType) {
        /*
        로그인에 따라 User를 가져오는 로직
         */
        User dummyUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        auctionSubscriptionRepository.findByUserAndAuction(dummyUser, auction)
                .ifPresent(auctionSubscriptionRepository::delete);
        List<String> fcmTokenList = userFcmTokenRepository.findUserFcmTokenListByUserId(dummyUser.getId()).stream()
                .map(UserFcmToken::getToken).collect(Collectors.toList());
        fcmService.unSubscribeAuction("Auction_" + auction.getId(), fcmTokenList);

    }
}
