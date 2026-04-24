package com.noorschool.auth.service;

import com.noorschool.auth.model.dto.OAuth2UserProfileDTO;
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

    /**
     * 신규 회원 약관 동의 전까지만 유효한 임시 가입 토큰 (15분).
     * DB에 회원 정보를 저장하기 전에 OAuth 프로필을 안전하게 전달하기 위해 사용.
     */
    public String createSignupToken(String googleSub, String email, String name, String profileImage) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(15, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(googleSub)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .id(UUID.randomUUID().toString())
                .claim("type", "signup")
                .claim("email", email)
                .claim("name", name)
                .claim("profileImage", profileImage != null ? profileImage : "")
                .signWith(secretKey)
                .compact();
    }

    /**
     * signupToken을 검증하고 OAuth 프로필을 추출한다.
     * 만료되거나 type이 signup이 아니면 예외를 던진다.
     */
    public OAuth2UserProfileDTO extractSignupProfile(String signupToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(signupToken)
                    .getPayload();

            if (!"signup".equals(claims.get("type"))) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "유효하지 않은 가입 토큰 타입입니다.");
            }

            return OAuth2UserProfileDTO.builder()
                    .googleSub(claims.getSubject())
                    .email(claims.get("email", String.class))
                    .name(claims.get("name", String.class))
                    .profileImage(claims.get("profileImage", String.class))
                    .build();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "가입 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해 주세요.");
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }
}
