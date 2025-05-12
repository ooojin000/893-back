package com.samyookgoo.palgoosam.search.repository;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query(value = """
            SELECT *
            FROM search_history s
            WHERE user_id = :userId AND is_deleted = false
            ORDER BY s.created_at DESC
            LIMIT 10
            """, nativeQuery = true)
    List<SearchHistory> findAllByUserId(@Param("userId") Long userId);
           
    @Query(value = """
            SELECT *
            FROM search_history
            WHERE keyword = :keyword AND user_id = :userId AND is_deleted = :isDeleted
            """, nativeQuery = true)
    SearchHistory findByKeywordAndUserAndIsDeleted(@Param("keyword") String keyword, @Param("userId") Long userId,
                                                   @Param("isDeleted") boolean isDeleted);
}
