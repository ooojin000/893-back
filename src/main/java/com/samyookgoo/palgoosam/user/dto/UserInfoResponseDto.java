package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private final String email;
    private final String name;
    private final String profileUrl;
    private final String provider;

    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(user.getEmail(), user.getName(), user.getProfileImage(), user.getProvider());
    }
}