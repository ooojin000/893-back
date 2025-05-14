package com.samyookgoo.palgoosam.notification.fcm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FcmTokenSaveRequestDto {
    @NotNull
    private String fcmToken;
}
