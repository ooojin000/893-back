package com.samyookgoo.palgoosam.notification.fcm.repository;

import com.samyookgoo.palgoosam.notification.fcm.domain.UserFcmToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    @Query(value = """
            SELECT *
            FROM user_fcm_token t
            WHERE t.user_id = :userId and t.token IS NOT NULL
            """, nativeQuery = true
    )
    List<UserFcmToken> findUserFcmTokenListByUserId(@Param("userId") Long userId);
}
