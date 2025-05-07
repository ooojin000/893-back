package com.samyookgoo.palgoosam.bid.repository;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query(value = """
            SELECT * 
            FROM bid b
            WHERE b.auction_id IN (:auctionIdList)
            ORDER BY b.auction_id, b.price DESC
            """, nativeQuery = true)
    List<Bid> findByAuctionIdList(@Param("auctionIdList") List<Long> auctionIdList);
}
