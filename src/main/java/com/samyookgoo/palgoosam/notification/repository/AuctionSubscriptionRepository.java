package com.samyookgoo.palgoosam.notification.repository;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.notification.domain.AuctionSubscription;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionSubscriptionRepository extends JpaRepository<AuctionSubscription, Long> {
    Optional<AuctionSubscription> findByUserAndAuction(User user, Auction auction);
}
