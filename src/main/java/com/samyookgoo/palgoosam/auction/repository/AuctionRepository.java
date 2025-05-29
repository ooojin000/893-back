package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findAllBySeller_Id(Long sellerId);

    Optional<Auction> findById(Long id);

    List<Auction> findByCategoryIdAndStatus(Long categoryId, AuctionStatus status);

    @Query("SELECT a FROM Auction a WHERE a.category.parent.id = :parentId AND a.status = :status")
    List<Auction> findByParentCategoryIdAndStatus(@Param("parentId") Long parentId,
                                                  @Param("status") AuctionStatus status);

    @Modifying
    @Query("""
                UPDATE Auction a
                SET a.status = 'active'
                WHERE a.status = 'pending'
                  AND a.startTime <= :now
            """)
    int updateStatusToActive(@Param("now") LocalDateTime now);

    @Modifying
    @Query("""
                UPDATE Auction a
                SET a.status = 'completed'
                WHERE a.status = 'active'
                  AND a.endTime <= :now
            """)
    int updateStatusToCompleted(@Param("now") LocalDateTime now);

    List<Auction> findTop12ByOrderByCreatedAtDesc();

    long countByStatus(AuctionStatus status);
}
