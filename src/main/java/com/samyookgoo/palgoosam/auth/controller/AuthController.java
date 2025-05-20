package com.samyookgoo.palgoosam.auth.controller;

import com.samyookgoo.palgoosam.auth.JwtTokenProvider;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.domain.UserJwtToken;
import com.samyookgoo.palgoosam.user.repository.UserJwtTokenRepository;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "인증", description = "JWT 인증 및 로그아웃 관련 API")
public class AuthController {
    private final JwtTokenProvider jwtProvider;
    private final UserJwtTokenRepository userJwtTokenRepository;
    private final AuthService authService;

    @Operation(
            summary = "액세스 토큰 갱신",
            description = "리프레시 토큰이 유효한 경우 새로운 액세스 토큰과 리프레시 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않거나 사용자 조회 실패")
    })
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
        String newRefreshToken = jwtProvider.refreshRefreshToken(providerId);

        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        UserJwtToken userJwtToken = userJwtTokenRepository.getReferenceById(user.getId());

        // 디비 검증
        if (!userJwtToken.getRefreshToken().equals(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 유효하지 않습니다."));
        }

        userJwtToken.setRefreshToken(newRefreshToken);
        userJwtTokenRepository.save(userJwtToken);

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

    @Operation(
            summary = "로그아웃",
            description = "로그인한 사용자의 토큰을 만료시키고, 관련된 쿠키와 DB 정보를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    @Transactional
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
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
        userJwtTokenRepository.deleteById(user.getId());

        // SecurityContext 비우기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(req, res, auth);
        }

        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
}
