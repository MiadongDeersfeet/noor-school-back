package com.noorschool.global.response;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 공통 API 응답을 생성하는 유틸 클래스
 *
 * 컨트롤러에서 ResponseData를 직접 생성하지 않고
 * 이 유틸을 통해 일관된 응답 포맷을 유지하기 위해 사용한다.
 */
public class ResponseUtil {

    /**
     * 기본 생성자 차단
     * - 유틸 클래스이므로 객체 생성 불필요
     */
    private ResponseUtil() {
    }

    /**
     * 성공 응답 (데이터 포함)
     *
     * @param message 사용자에게 전달할 메시지
     * @param data    실제 응답 데이터
     * @return ResponseEntity<ResponseData<T>>
     *
     * 사용 예:
     * return ResponseUtil.ok("조회 성공", data);
     */
    public static <T> ResponseEntity<ResponseData<T>> ok(String message, T data) {

        ResponseData<T> response = ResponseData.<T>builder()
                .message(message) // 응답 메시지
                .data(data)       // 실제 데이터
                .success(true)    // 성공 여부
                .timestamp(LocalDateTime.now().toString()) // 응답 시각
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 성공 응답 (데이터 없음)
     *
     * @param message 사용자에게 전달할 메시지
     * @return ResponseEntity<ResponseData<Void>>
     *
     * 사용 예:
     * return ResponseUtil.ok("삭제 성공");
     */
    public static ResponseEntity<ResponseData<Void>> ok(String message) {

        ResponseData<Void> response = ResponseData.<Void>builder()
                .message(message)
                .data(null)       // 데이터 없음
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.ok(response);
    }
}