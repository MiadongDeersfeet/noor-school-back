package com.noorschool.word.controller;

import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/words")
public class WordController {
	
	@GetMapping
	public ResponseEntity<ApiResponseDTO<String>> selectRandomWords() {
		log.info("WordController /api/words endpoint called.");
		return ApiResponseDTO.toResponseEntity(
				ResultCode.SUCCESS,
				"단어 API 진입 성공",
				"단어 조회 API 구현 예정"
		);
	}

}
