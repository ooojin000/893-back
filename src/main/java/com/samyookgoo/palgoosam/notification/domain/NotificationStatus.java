package com.samyookgoo.palgoosam.notification.domain;

import com.samyookgoo.palgoosam.notification.constant.NotificationStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(NotificationStatusId.class)
@Table(name = "notification_status")
public class NotificationStatus {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "notification_history_id")
    private Long notificationHistoryId;

    private Boolean isRead = false;

    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    private NotificationStatusType notificationStatusType;

    private Integer retryCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
