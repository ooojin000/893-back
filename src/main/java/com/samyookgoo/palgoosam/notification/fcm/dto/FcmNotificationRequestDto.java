package com.samyookgoo.palgoosam.notification.fcm.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FcmNotificationRequestDto {
    private Long userId;
    private Long auctionId;
    private Long notificationId;
    private String auctionTitle;
    private String message;
    private LocalDateTime createdAt;
    private String messageType;
    private String imageUrl;
}
