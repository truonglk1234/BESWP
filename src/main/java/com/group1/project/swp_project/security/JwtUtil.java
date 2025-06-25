package com.group1.project.swp_project.security;

import java.util.Date;
import java.util.List;

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
    private final long jwtExpirationMs = 7 * 24 * 60 * 60 * 1000L; // 7 ngày
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

    // ✅ Tạo token mới
    public String generateToken(int userId, String email, String role, String name) {
        return Jwts.builder()
                .setSubject(email) // sub = email
                .claim("userId", userId)
                .claim("Name", name)
                .claim("role", role)
                .claim("authorities", List.of(role))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Trích xuất email từ token (vì sub là email)
    public String extractUserEmail(String token) {
        checkBlacklisted(token);
        return getClaims(token).getSubject(); // subject = email
    }

    // ✅ Trích xuất quyền (role/authorities)
    public List<String> extractAuthorities(String token) {
        checkBlacklisted(token);
        return getClaims(token).get("authorities", List.class);
    }

    public int extractUserId(String token) {
        checkBlacklisted(token);
        return getClaims(token).get("userId", Integer.class);
    }

    // ✅ Trích xuất tất cả claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Kiểm tra token có bị vô hiệu hóa không
    private void checkBlacklisted(String token) {
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new RuntimeException("Token has been invalidated");
        }
    }

    // ✅ Vô hiệu hóa token
    public void invalidateToken(String token) {
        tokenBlacklistService.blacklistToken(token);
    }
}
