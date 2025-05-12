package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.samyookgoo.palgoosam.auth.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        User user = authService.getCurrentUser();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "profileUrl", user.getProfileImage(),
                "provider", user.getProvider()
        ));
    }
}