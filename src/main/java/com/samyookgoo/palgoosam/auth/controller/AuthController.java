package com.samyookgoo.palgoosam.auth.controller;

import com.samyookgoo.palgoosam.auth.JwtTokenProvider;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.domain.UserOauthToken;
import com.samyookgoo.palgoosam.user.repository.UserOauthTokenRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final JwtTokenProvider jwtProvider;
    private final UserOauthTokenRepository userOauthTokenRepository;
    private final AuthService authService;

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue("refreshToken") String refreshToken
    ) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 유효하지 않습니다."));
        }

        String providerId;
        try {
            providerId = jwtProvider.getUserProviderIdFromRefreshToken(refreshToken);
        } catch (JwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰에 조회되는 유저가 없습니다."));
        }

        String newAccessToken = jwtProvider.refreshAccessToken(providerId);
        String newRefreshToken = jwtProvider.refreshAccessToken(newAccessToken);

        User user = authService.getCurrentUser();

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

    @Transactional
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        User user = authService.getCurrentUser();

        // 쿠키 삭제
        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).path("/")
                .maxAge(0).sameSite("Lax").build();

        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).path("/auth/refresh")
                .maxAge(0).sameSite("Lax").build();

        res.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
        res.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());

        // DB에 저장된 토큰 삭제
        userOauthTokenRepository.deleteById(user.getId());

        // SecurityContext 비우기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(req, res, auth);
        }

        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
}
