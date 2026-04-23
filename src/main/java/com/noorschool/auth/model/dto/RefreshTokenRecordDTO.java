package com.noorschool.auth.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RefreshTokenRecordDTO {
    private Long tokenId;
    private Long memberId;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private String isRevoked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
