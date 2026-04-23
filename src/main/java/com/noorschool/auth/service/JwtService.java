package com.noorschool.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final String issuer;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    public JwtService(
            @Value("${app.auth.jwt.secret}") String secret,
            @Value("${app.auth.jwt.issuer}") String issuer,
            @Value("${app.auth.jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
            @Value("${app.auth.jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    public String createAccessToken(Long memberId, String email, String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .id(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("role", role)
                .claim("type", "access")
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(refreshTokenExpirationSeconds, ChronoUnit.SECONDS);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .id(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    public Long extractMemberIdFromRefreshToken(String refreshToken) {
        Claims claims = parseRefreshClaims(refreshToken);
        return Long.valueOf(claims.getSubject());
    }

    private Claims parseRefreshClaims(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            Object type = claims.get("type");
            if (!"refresh".equals(type)) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "유효하지 않은 리프레시 토큰 타입입니다.");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }
}
