package com.samyookgoo.palgoosam.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("accessToken".equals(c.getName())) {
                    token = c.getValue();
                }
            }
        }

        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsername(token);
            Authentication auth = jwtProvider.getAuthentication(username);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/login") || p.startsWith("/oauth2");
    }
}
