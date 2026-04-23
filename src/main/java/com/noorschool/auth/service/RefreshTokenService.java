package com.noorschool.auth.service;

import com.noorschool.auth.model.dto.RefreshTokenRecordDTO;
import com.noorschool.auth.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshTokenRecordDTO findActiveToken(String refreshToken) {
        return refreshTokenMapper.selectActiveByToken(refreshToken);
    }

    @Transactional
    public void rotateRefreshToken(Long memberId, String refreshToken, long refreshTokenExpirationSeconds) {
        refreshTokenMapper.revokeAllByMemberId(memberId);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds);
        refreshTokenMapper.insertRefreshToken(memberId, refreshToken, expiresAt);
    }

    @Transactional
    public void revokeByToken(String refreshToken) {
        refreshTokenMapper.revokeByToken(refreshToken);
    }
}
