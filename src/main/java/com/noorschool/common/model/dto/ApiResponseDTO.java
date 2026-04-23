package com.noorschool.common.model.dto;

import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;

/**
 * 모든 API 응답을 감싸는 공통 DTO 입니다.
 *
 * @param <T> 응답 본문 데이터 타입
 */
public class ApiResponseDTO<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public ApiResponseDTO() {
    }

    public ApiResponseDTO(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 성공 응답 생성 헬퍼입니다.
     */
    public static <T> ApiResponseDTO<T> success(T data) {
        return success(ResultCode.SUCCESS, data);
    }

    /**
     * ResultCode를 지정해 성공 응답을 생성합니다.
     */
    public static <T> ApiResponseDTO<T> success(ResultCode resultCode, T data) {
        return new ApiResponseDTO<>(true, resultCode.getCode(), resultCode.getMessage(), data);
    }

    /**
     * ResultCode를 지정해 사용자 정의 메시지로 성공 응답을 생성합니다.
     */
    public static <T> ApiResponseDTO<T> success(ResultCode resultCode, String message, T data) {
        return new ApiResponseDTO<>(true, resultCode.getCode(), message, data);
    }

    /**
     * 실패 응답 생성 헬퍼입니다. (기본 메시지 사용)
     */
    public static ApiResponseDTO<ErrorResponseDTO> fail(ResultCode resultCode) {
        return fail(resultCode, resultCode.getMessage());
    }

    /**
     * 실패 상세 메시지를 포함한 응답 생성 헬퍼입니다.
     */
    public static ApiResponseDTO<ErrorResponseDTO> fail(ResultCode resultCode, String detailMessage) {
        return fail(resultCode, ErrorResponseDTO.of(detailMessage));
    }

    /**
     * 에러 상세 DTO를 포함한 실패 응답 생성 헬퍼입니다.
     */
    public static ApiResponseDTO<ErrorResponseDTO> fail(ResultCode resultCode, ErrorResponseDTO errorResponseDTO) {
        return new ApiResponseDTO<>(false, resultCode.getCode(), resultCode.getMessage(), errorResponseDTO);
    }

    /**
     * ResultCode의 HTTP 상태를 사용해 ResponseEntity를 생성합니다.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> toResponseEntity(ResultCode resultCode, T data) {
        return ResponseEntity.status(resultCode.getStatus()).body(success(resultCode, data));
    }

    /**
     * ResultCode의 HTTP 상태를 사용해 사용자 정의 메시지 성공 ResponseEntity를 생성합니다.
     */
    public static <T> ResponseEntity<ApiResponseDTO<T>> toResponseEntity(
            ResultCode resultCode,
            String message,
            T data
    ) {
        return ResponseEntity.status(resultCode.getStatus()).body(success(resultCode, message, data));
    }

    /**
     * ResultCode의 HTTP 상태를 사용해 실패 ResponseEntity를 생성합니다.
     */
    public static ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> toErrorResponseEntity(ResultCode resultCode) {
        return ResponseEntity.status(resultCode.getStatus()).body(fail(resultCode));
    }

    /**
     * ResultCode의 HTTP 상태를 사용해 상세 메시지 포함 실패 ResponseEntity를 생성합니다.
     */
    public static ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> toErrorResponseEntity(
            ResultCode resultCode,
            String detailMessage
    ) {
        return ResponseEntity.status(resultCode.getStatus()).body(fail(resultCode, detailMessage));
    }

    /**
     * ResultCode의 HTTP 상태를 사용해 에러 DTO 포함 실패 ResponseEntity를 생성합니다.
     */
    public static ResponseEntity<ApiResponseDTO<ErrorResponseDTO>> toErrorResponseEntity(
            ResultCode resultCode,
            ErrorResponseDTO errorResponseDTO
    ) {
        return ResponseEntity.status(resultCode.getStatus()).body(fail(resultCode, errorResponseDTO));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
