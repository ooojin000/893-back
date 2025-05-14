package com.samyookgoo.palgoosam.notification.repository;


import com.samyookgoo.palgoosam.notification.domain.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
}
