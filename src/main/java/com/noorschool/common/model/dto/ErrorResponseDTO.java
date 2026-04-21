package com.noorschool.common.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 실패 응답에서 추가 상세 정보를 전달할 DTO 입니다.
 */
public class ErrorResponseDTO {
    private List<String> details = new ArrayList<>();

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(List<String> details) {
        this.details = details;
    }

    public static ErrorResponseDTO of(String detail) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.getDetails().add(detail);
        return errorResponseDTO;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
