package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.samyookgoo.palgoosam.auth.service.AuthService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        User user = authService.getCurrentUser();

        UserInfoResponseDto dto = new UserInfoResponseDto(
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getProvider()
        );

        return ResponseEntity.ok(dto);
    }
}