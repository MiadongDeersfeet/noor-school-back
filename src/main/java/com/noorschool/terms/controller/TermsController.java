package com.noorschool.terms.controller;

import com.noorschool.auth.model.dto.AuthTokenResponseDTO;
import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.terms.model.dto.TermsAgreementRequestDTO;
import com.noorschool.terms.model.dto.TermsDTO;
import com.noorschool.terms.service.TermsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TermsDTO>>> getTerms() {
        return ResponseEntity.ok(ApiResponseDTO.success(termsService.getActiveTerms()));
    }

    /**
     * 신규 회원 약관 동의 + 회원가입 완료.
     * signupToken 검증 → TB_MEMBER / TB_TERMS_AGREEMENT / TB_REFRESH_TOKEN 단일 트랜잭션 처리.
     */
    @PostMapping("/agree")
    public ResponseEntity<ApiResponseDTO<AuthTokenResponseDTO>> agreeTerms(
            @RequestBody TermsAgreementRequestDTO request,
            HttpServletResponse httpResponse
    ) {
        AuthTokenResponseDTO authResponse = termsService.completeSignup(request, httpResponse);
        return ResponseEntity.ok(ApiResponseDTO.success(authResponse));
    }
}
