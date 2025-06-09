package com.group1.project.swp_project.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.group1.project.swp_project.service.TokenBlacklistService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    private final long jwtExpirationMs = 86400000; // 24 hours in milliseconds
    private final String SECRET_KEY = "my_secret_key_12345678901234567890123456789012";
    private SecretKey key;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtUtil(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String userPhone, String role) {
        return Jwts.builder()
                .setSubject(userPhone)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserPhone(String token) {
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new RuntimeException("Token has been invalidated");
        }
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new RuntimeException("Token has been invalidated");
        }
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void invalidateToken(String token) {
        tokenBlacklistService.blacklistToken(token);
    }
}
