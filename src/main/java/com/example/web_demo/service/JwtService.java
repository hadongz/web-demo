package com.example.web_demo.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:1234567810}")
    private String jwtSecret;

    @Value("${jwt.expiration:1800}")
    private int jwtExpirationInSeconds;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String createToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationInSeconds * 1000L);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean isTokenValid(String token) {
        Claims claims = getClaims(token);
        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .decryptWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
