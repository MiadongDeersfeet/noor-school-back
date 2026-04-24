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
    private boolean isNewMember;
    /** 신규 회원 전용. 약관 동의 완료 전까지 DB에 저장되지 않으며, 이 토큰으로 약관 동의 API를 호출한다. */
    private String signupToken;
}
