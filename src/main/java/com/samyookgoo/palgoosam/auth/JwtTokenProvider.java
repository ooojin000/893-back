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

    private final Key accessKey;
    private final Key refreshKey;
    private final long accessValidityMs;
    private final long refreshValidityMs;

    public JwtTokenProvider() {
        this.accessKey = Keys.hmacShaKeyFor("fnfjhdkdmndnfjcmnaoqndjduekdsndjxjemejwjdsx".getBytes());
        this.refreshKey = Keys.hmacShaKeyFor("goormdjhwdjfnvjxmxmasjdaksdfdasdmaskdnsadqdwqwdwgfx".getBytes());
        this.accessValidityMs = 1000 * 60 * 60;
        this.refreshValidityMs = 1000L * 60 * 60 * 24 * 7;
    }

    public String generateAccessToken(Authentication authentication) {
        String providerId = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessValidityMs);

        return Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String providerId = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshValidityMs);

        return Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshAccessToken(String providerId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessValidityMs);

        return Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshRefreshToken(String providerId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshValidityMs);

        return Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserProviderIdFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserProviderIdFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String username) {
        UserDetails user = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("")
                        .authorities("ROLE_USER")
                        .build();
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    public long getAccessValidityMs() {
        return accessValidityMs;
    }

    public long getRefreshValidityMs() {
        return refreshValidityMs;
    }
}