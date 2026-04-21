package com.noorschool.auth.controller;

import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증(Auth) 도메인 테스트용 컨트롤러입니다.
 * <p>
 * 아직 OAuth2/JWT 구현 전 단계이므로
 * 공통 응답 규약(ApiResponseDTO + ResultCode) 동작 확인에 집중합니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 성공 응답 포맷 테스트용 엔드포인트입니다.
     */
    @GetMapping("/test-success")
    public ResponseEntity<ApiResponseDTO<String>> testSuccess() {
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "auth test success");
    }

    /**
     * 실패 응답 포맷 테스트용 엔드포인트입니다.
     * BusinessException이 전역 핸들러에서 공통 형식으로 변환됩니다.
     */
    @GetMapping("/test-fail")
    public ResponseEntity<ApiResponseDTO<Void>> testFail() {
        throw new BusinessException(ResultCode.INVALID_REQUEST, "테스트 실패 요청입니다.");
    }
}
