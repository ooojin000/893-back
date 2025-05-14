package com.samyookgoo.palgoosam.notification.dto;

import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private List<SubscriptionType> subscriptionTypeList;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Long auctionId;
    private Boolean isRead;
    private String imageUrl;
}
