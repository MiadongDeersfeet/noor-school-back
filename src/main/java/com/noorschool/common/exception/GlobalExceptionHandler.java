package com.noorschool.common.exception;

import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.dto.ErrorResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역 예외를 공통 응답 포맷으로 변환하는 핸들러입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 계층에서 던진 BusinessException 처리.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleBusinessException(BusinessException ex) {
        return ApiResponseDTO.toErrorResponseEntity(ex.getResultCode(), ex.getMessage());
    }

    /**
     * @Valid 바인딩 실패 처리.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleValidationException(Exception ex) {
        return ApiResponseDTO.toErrorResponseEntity(ResultCode.INVALID_REQUEST, "요청 값 검증에 실패했습니다.");
    }

    /**
     * 처리하지 못한 예외의 마지막 안전망.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> handleException(Exception ex) {
        return ApiResponseDTO.toErrorResponseEntity(ResultCode.INTERNAL_ERROR);
    }
}
