package com.samyookgoo.palgoosam.notification.repository;

import com.samyookgoo.palgoosam.notification.domain.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {
}
