package com.samyookgoo.palgoosam.auth.controller;

import com.samyookgoo.palgoosam.auth.JwtTokenProvider;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.domain.UserOauthToken;
import com.samyookgoo.palgoosam.user.repository.UserOauthTokenRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final JwtTokenProvider jwtProvider;     // JWT 생성/검증 유틸
    private final UserRepository userRepository;
    private final UserOauthTokenRepository userOauthTokenRepository;

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue("refreshToken") String refreshToken
    ) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 유효하지 않습니다."));
        }

        String providerId = jwtProvider.getUserProviderIdFromRefreshToken(refreshToken);

        String newAccessToken = jwtProvider.refreshAccessToken(providerId);
        String newRefreshToken = jwtProvider.refreshAccessToken(newAccessToken);

        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserOauthToken userOauthToken = userOauthTokenRepository.getReferenceById(user.getId());

        // 디비 검증
        if(!userOauthToken.getRefreshToken().equals(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 유효하지 않습니다."));
        }

        userOauthToken.setRefreshToken(newRefreshToken);
        userOauthTokenRepository.save(userOauthToken);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtProvider.getAccessValidityMs() / 1000)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true).secure(true).path("/auth/refresh")
                .maxAge(jwtProvider.getRefreshValidityMs() / 1000)
                .sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}
