package com.samyookgoo.palgoosam.bid.repository;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);

    @Query("SELECT MAX(b.price) FROM Bid b WHERE b.auction.id = :auctionId AND b.isDeleted = false")
    Integer findMaxBidPriceByAuctionId(Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId " +
            "AND b.isDeleted = false " +
            "ORDER BY b.price DESC LIMIT 1")
    Optional<Bid> findTopValidBidByAuctionId(Long auctionId);
}
