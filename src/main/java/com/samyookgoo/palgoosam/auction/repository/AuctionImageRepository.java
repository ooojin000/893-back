package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {
    List<AuctionImage> findByAuctionId(Long auctionId);

    @Query("SELECT a FROM AuctionImage a " +
            "WHERE a.auction.id = :auctionId AND a.imageSeq = 0")
    Optional<AuctionImage> findMainImageByAuctionId(@Param("auctionId") Long auctionId);

}
