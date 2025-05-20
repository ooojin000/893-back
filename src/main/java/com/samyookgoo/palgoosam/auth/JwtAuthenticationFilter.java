package com.samyookgoo.palgoosam.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain
    ) throws java.io.IOException, jakarta.servlet.ServletException {
        String accessToken = null;
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }
        log.info("Access token: {}", accessToken);
        // 토큰이 없거나 검증에 실패하면 401 + 메시지 응답
        if (accessToken == null || !jwtProvider.validateAccessToken(accessToken)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json; charset=UTF-8");
            String body = "{\"message\": \"액세스 토큰이 만료됐습니다.\"}";
            res.getWriter().write(body);
            return;
        }
        // 검증 성공 시에만 인증 세팅
        String providerId = jwtProvider.getUserProviderIdFromAccessToken(accessToken);
        Authentication auth = jwtProvider.getAuthentication(providerId);
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("path: {}", path, path.startsWith("/api/auctions/search"));
        // 로그인·OAuth2 콜백
        if (path.startsWith("/login") || path.startsWith("/oauth2")) {
            return true;
        }

        // 검색 API → JWT 검증 건너뛰기
        if ("GET".equals(request.getMethod()) && "/api/auctions/search".equals(path)) {
            return true;
        }

        // 카테고리 API
        if ("GET".equals(request.getMethod()) && "/api/category".equals(path)) {
            return true;
        }
        
        // 업로드된 이미지
        if ("GET".equals(request.getMethod()) && path.startsWith("/uploads/")) {
            return true;
        }

        if ("GET".equals(request.getMethod()) && path.startsWith("/api/auctions/**")) {
            return true;
        }
        return false;
    }
}