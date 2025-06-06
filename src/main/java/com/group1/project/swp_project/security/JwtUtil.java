package com.group1.project.swp_project.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

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

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String userPhone, String role) {

        // Logic to generate JWT token using the secret and expiration time
        // This is a placeholder, actual implementation will depend on your JWT library
        return Jwts.builder()
                .setSubject(userPhone)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserPhone(String token) {
        // Logic to extract user phone from the JWT token
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        // Logic to extract role from the JWT token
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        // Logic to parse the JWT token and return claims
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
