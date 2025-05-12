package com.samyookgoo.palgoosam.bid.repository;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findAllByBidder_Id(Long bidderId);

    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);

    @Query("SELECT MAX(b.price) FROM Bid b WHERE b.auction.id = :auctionId AND b.isDeleted = false")
    Integer findMaxBidPriceByAuctionId(Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId " +
            "AND b.isDeleted = false " +
            "ORDER BY b.price DESC LIMIT 1")
    Optional<Bid> findTopValidBidByAuctionId(Long auctionId);

    Optional<Bid> findByAuctionIdAndIsWinningTrue(Long auctionId);
  
    @Query(value = """
            SELECT * 
            FROM bid b
            WHERE b.auction_id IN (:auctionIdList)
            ORDER BY b.auction_id, b.price DESC
            """, nativeQuery = true)
    List<Bid> findByAuctionIdList(@Param("auctionIdList") List<Long> auctionIdList);
}
