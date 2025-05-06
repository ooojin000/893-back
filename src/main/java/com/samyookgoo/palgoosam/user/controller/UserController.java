package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.auth.service.CustomOauth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/user-info")
    public ResponseEntity<?> getMyInfo(
            @AuthenticationPrincipal CustomOauth2UserDetails principal
    ) {
        return ResponseEntity.ok(
                new java.util.HashMap<>() {{
                    put("email", principal.getUserEmail());
                    put("name",  principal.getName());
                }}
        );
    }
}