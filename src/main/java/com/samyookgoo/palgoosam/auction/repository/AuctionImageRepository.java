package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {
    List<AuctionImage> findByAuctionId(Long auctionId);
  
     @Query(value = """
            SELECT i.*
            FROM auction_image i
            WHERE i.auction_id IN (:auctionIdList) AND i.image_seq = 0
            """, nativeQuery = true)
    List<AuctionImage> findThumbnailsByAuctionIds(@Param("auctionIdList") List<Long> auctionIdList);
}
