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
        // TODO: 로직 변경된 부분 확인 후 추후 삭제
//        log.info("Access token: {}", accessToken);
//        // 토큰이 없거나 검증에 실패하면 401 + 메시지 응답
//
//        if (accessToken == null) {
//            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            res.setContentType("application/json; charset=UTF-8");
//            String body = "{\"message\": \"액세스 토큰이 없습니다.\"}";
//            res.getWriter().write(body);
//            return;
//        }
//
//        if (!jwtProvider.validateAccessToken(accessToken)) {
//            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            res.setContentType("application/json; charset=UTF-8");
//            String body = "{\"message\": \"액세스 토큰이 만료됐습니다.\"}";
//            res.getWriter().write(body);
//            return;
//        }
//        // 검증 성공 시에만 인증 세팅
//        String providerId = jwtProvider.getUserProviderIdFromAccessToken(accessToken);
//        Authentication auth = jwtProvider.getAuthentication(providerId);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        chain.doFilter(req, res);

        // 토큰이 있을 경우만 검증
        if (accessToken != null) {
            if (jwtProvider.validateAccessToken(accessToken)) {
                String providerId = jwtProvider.getUserProviderIdFromAccessToken(accessToken);
                Authentication auth = jwtProvider.getAuthentication(providerId);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 토큰은 있지만 유효하지 않음 → 401 반환
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().write("{\"message\": \"액세스 토큰이 유효하지 않거나 만료됐습니다.\"}");
                return;
            }
        }

        // 비회원이거나 유효한 토큰일 경우 → 필터 체인 계속 진행
        chain.doFilter(req, res);
    }
}