package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.category " +
            "JOIN FETCH a.seller " +
            "WHERE a.id = :auctionId")
    Optional<Auction> findByIdWithCategoryAndSeller(@Param("auctionId") Long auctionId);

}
