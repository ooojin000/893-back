package com.samyookgoo.palgoosam.notification.dto;

import com.samyookgoo.palgoosam.notification.subscription.constant.SubscriptionType;
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
public class NotificationRequestDto {
    private Long auctionId;
    private String title;
    private String message;
    private SubscriptionType subscriptionType;
    private String imageUrl;
}
