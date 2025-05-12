package com.samyookgoo.palgoosam.user.dto;

public class ApiResponseDto {
    private final String message;

    public ApiResponseDto(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
