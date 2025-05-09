package com.samyookgoo.palgoosam.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Getter
public class JwtTokenProvider {

    private final Key key;
    private final long validityInMilliseconds;

    public JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor("fnfjhdkdmndnfjcmnaoqndjduekdsndjxjemejwjdsx".getBytes());
        this.validityInMilliseconds = 86400000;
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String username) {
        // 간단하게 UsernamePasswordAuthenticationToken 생성
        UserDetails user =
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("")          // JWT만으로 인증하므로 빈 비밀번호
                        .authorities("ROLE_USER")
                        .build();
        return new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
    }

    public long getValidityMs() {
        return validityInMilliseconds;
    }
}