package com.samyookgoo.palgoosam.notification.repository;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.notification.domain.AuctionSubscription;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionSubscriptionRepository extends JpaRepository<AuctionSubscription, Long> {
    List<AuctionSubscription> findAllByUserAndAuction(User user, Auction auction);
}
