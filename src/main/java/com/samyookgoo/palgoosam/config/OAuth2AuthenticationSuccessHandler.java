package com.samyookgoo.palgoosam.config;

import com.samyookgoo.palgoosam.auth.service.JwtTokenProvider;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException {
        // 1) JWT 생성
        String token = jwtProvider.generateToken(auth);

        // 2) HttpOnly 쿠키 설정
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtProvider.getValidityMs()/1000)
                .sameSite("Lax")
                .build();
        res.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 3) 프론트로 리다이렉트
        String frontUrl = "http://localhost:3000";
        res.sendRedirect(frontUrl + "?loginSuccess");
    }
}