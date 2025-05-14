package com.samyookgoo.palgoosam.notification.repository;

import com.samyookgoo.palgoosam.notification.domain.NotificationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {

    @Query(value = """
            SELECT * 
            FROM notification_status
            WHERE user_id = :userId AND is_deleted = false
            """, nativeQuery = true)
    List<NotificationStatus> findAllByUserIdAndIsDeletedFalse(Long userId);

    Optional<NotificationStatus> findByUserIdAndNotificationHistoryId(Long userId, Long notificationHistoryId);

}
