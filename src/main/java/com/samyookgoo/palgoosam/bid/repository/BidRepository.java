package com.samyookgoo.palgoosam.bid.repository;

import com.samyookgoo.palgoosam.auction.projection.AuctionBidCount;
import com.samyookgoo.palgoosam.auction.projection.TopWinningBid;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.projection.AuctionMaxBid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("""
                SELECT b.auction.id AS auctionId,
                       MAX(b.price)     AS maxPrice
                FROM Bid b
                WHERE b.auction.id IN :auctionIds
                GROUP BY b.auction.id
            """)
    List<AuctionMaxBid> findMaxBidPricesByAuctionIds(@Param("auctionIds") List<Long> auctionIds);

    List<Bid> findAllByBidder_Id(Long bidderId);

    List<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);

    @Query("SELECT MAX(b.price) FROM Bid b WHERE b.auction.id = :auctionId AND b.isDeleted = false")
    Integer findMaxBidPriceByAuctionId(Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId " +
            "AND b.isDeleted = false " +
            "ORDER BY b.price DESC LIMIT 1")
    Optional<Bid> findTopValidBidByAuctionId(Long auctionId);

    Optional<Bid> findByAuctionIdAndIsWinningTrue(Long auctionId);

    boolean existsByAuctionIdAndIsWinningTrue(Long auctionId);

    @Query(value = """
            SELECT * 
            FROM bid b
            WHERE b.auction_id IN (:auctionIdList)
            ORDER BY b.auction_id, b.price DESC
            """, nativeQuery = true)
    List<Bid> findByAuctionIdList(@Param("auctionIdList") List<Long> auctionIdList);

    Integer countByAuctionIdAndIsDeletedFalse(Long auctionId);

    @Query("SELECT COUNT(DISTINCT b.bidder.id) FROM Bid b WHERE b.auction.id = :auctionId AND b.isDeleted = false")
    Integer countDistinctBidderByAuctionId(Long auctionId);

    Boolean existsByAuctionIdAndBidderIdAndIsDeletedTrue(Long auctionId, Long bidderId);

    List<Bid> findByAuctionId(Long auctionId);

    @Query("SELECT b.auction.id AS auctionId, COUNT(b.id) AS bidCount FROM Bid b WHERE b.auction.id IN :auctionIds GROUP BY b.auction.id")
    List<AuctionBidCount> countBidsByAuctionIds(@Param("auctionIds") List<Long> auctionIds);


    @Query("""
            SELECT 
                a.id AS auctionId,
                a.title AS title,
                a.basePrice AS basePrice,
                MAX(b.price) AS itemPrice,
                img.url AS thumbnailUrl,
                b.bidder.name AS buyer
            FROM Bid b
            JOIN b.auction a
            LEFT JOIN AuctionImage img ON img.auction.id = a.id AND img.imageSeq = 0
            WHERE b.isWinning = true
              AND a.status = 'COMPLETED'
              AND a.endTime >= :sevenDaysAgo
            GROUP BY a.id, a.title, a.basePrice, img.url, b.bidder.name
            ORDER BY itemPrice DESC
            """)
    List<TopWinningBid> findTop5WinningBids(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

}
