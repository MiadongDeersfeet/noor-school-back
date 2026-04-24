package com.noorschool.terms.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TermsAgreementRequestDTO {
    /** 신규 회원 전용. OAuth 콜백에서 발급한 15분짜리 임시 토큰. */
    private String signupToken;
    private List<AgreementItem> agreements;

    @Getter
    @NoArgsConstructor
    public static class AgreementItem {
        private Long termsId;
        private boolean agreed;
    }
}
