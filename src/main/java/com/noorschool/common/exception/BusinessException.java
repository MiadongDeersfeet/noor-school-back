package com.noorschool.common.exception;

import com.noorschool.common.model.vo.ResultCode;

/**
 * 서비스 레이어에서 비즈니스 오류를 던질 때 사용하는 공통 예외입니다.
 */
public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
