package com.samyookgoo.palgoosam.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private final String email;
    private final String name;
    private final String profileUrl;
    private final String provider;
}