package com.samyookgoo.palgoosam.user.repository;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    int countByAuctionId(Long auctionId);

    boolean existsByUserIdAndAuctionId(Long userId, Long auctionId);

    boolean existsByUserAndAuction(User user, Auction auction);

    Optional<Scrap> findByUserAndAuction(User user, Auction auction);
}
