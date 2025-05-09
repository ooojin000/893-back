package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query(value = """
            SELECT *
            FROM search_history
            WHERE keyword = :keyword AND user_id = :userId AND is_deleted = :isDeleted
            """, nativeQuery = true)
    SearchHistory findByKeywordAndUserAndIsDeleted(@Param("keyword") String keyword, @Param("userId") Long userId,
                                                   @Param("isDeleted") boolean isDeleted);
}
