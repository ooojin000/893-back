package com.samyookgoo.palgoosam.config;

import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest req, HttpServletResponse res,
            AuthenticationException ex
    ) throws IOException {
        log.error("OAuth2 로그인 실패", ex);
        res.sendRedirect("/login?error");
    }
}