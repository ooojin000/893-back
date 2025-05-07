package com.samyookgoo.palgoosam.bid.repository;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);
}
