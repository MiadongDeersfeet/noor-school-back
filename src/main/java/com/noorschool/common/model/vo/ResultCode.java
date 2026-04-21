package com.noorschool.common.model.vo;

import org.springframework.http.HttpStatus;

/**
 * 공통 응답에서 사용할 결과 코드 enum 입니다.
 * <p>
 * - status: HTTP 응답 상태 코드
 * - code: 클라이언트가 분기 처리하기 쉬운 문자열 코드
 * - message: 사용자/로그에 표시할 기본 메시지
 */
public enum ResultCode {
    SUCCESS(HttpStatus.OK, "SUCCESS", "요청이 성공했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "대상을 찾을 수 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ResultCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
