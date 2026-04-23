package com.noorschool.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenResponseDTO {
    private Long memberId;
    private String email;
    private String name;
    private String role;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;
    private long refreshTokenExpiresIn;
}
