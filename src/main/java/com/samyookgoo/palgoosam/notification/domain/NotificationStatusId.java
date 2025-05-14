package com.samyookgoo.palgoosam.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationStatusId {
    private Long userId;
    private Long notificationHistoryId;
}
