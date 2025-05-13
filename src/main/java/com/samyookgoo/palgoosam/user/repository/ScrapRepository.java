package com.samyookgoo.palgoosam.user.repository;

import com.samyookgoo.palgoosam.user.domain.Scrap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findAllByUser_Id(Long auctionId);

    @Query(value = """
            SELECT *
            FROM scrap s
            WHERE s.auction_id IN (:auctionIdList)
            """, nativeQuery = true)
    List<Scrap> findByAuctionIdList(@Param("auctionIdList") List<Long> auctionIdList);
}
