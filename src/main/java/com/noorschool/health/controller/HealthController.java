package com.noorschool.health.controller;

import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;
// HTTP GET 요청을 처리하기 위한 어노테이션
import org.springframework.web.bind.annotation.GetMapping;

// 공통 URL 경로를 설정하기 위한 어노테이션
import org.springframework.web.bind.annotation.RequestMapping;

// REST API 컨트롤러임을 명시 (JSON/문자열 바로 반환)
import org.springframework.web.bind.annotation.RestController;

// 시간 정보 반환용
import java.time.LocalDateTime;
import java.util.Map;

/**
 * [HealthController]
 * 
 * 👉 용도:
 * 1. 서버가 정상적으로 실행 중인지 확인
 * 2. 프론트와 백엔드 연결 테스트
 * 3. 배포 후 서버 상태 체크
 * 4. CORS / 포트 / API 경로 문제 확인
 */
@RestController

// 모든 API 앞에 /api 공통 prefix
@RequestMapping("/api")
public class HealthController {

    /**
     * [기본 헬스 체크 API]
     * 
     * 👉 호출:
     * GET http://localhost:8080/api/health
     * 
     * 👉 용도:
     * 가장 간단한 서버 생존 확인
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<String>> health() {
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "헬스 체크 성공", "noorschool backend ok");
    }


    /**
     * [확장형 헬스 체크 API]
     * 
     * 👉 호출:
     * GET http://localhost:8080/api/health/detail
     * 
     * 👉 용도:
     * - JSON 형태로 상태 확인
     * - 시간 포함
     * - 추후 DB 상태 등 추가 가능
     */
    @GetMapping("/health/detail")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> healthDetail() {
        Map<String, Object> detail = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "헬스 상세 조회 성공", detail);
    }
}