package com.noorschool.quiz.controller;

import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.noorschool.quiz.model.dto.QuizSetResponseDTO;
import com.noorschool.quiz.model.dto.QuizSubmitRequestDTO;
import com.noorschool.quiz.model.dto.QuizSubmitResponseDTO;
import com.noorschool.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/random")
    public ResponseEntity<ApiResponseDTO<QuizSetResponseDTO>> getRandomQuizzes() {
        QuizSetResponseDTO response = quizService.createRandomQuizSet(5);
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "랜덤 퀴즈 조회 성공", response);
    }
    
    @PostMapping("/submit")
    public ResponseEntity<ApiResponseDTO<QuizSubmitResponseDTO>> submitAnswer(@RequestBody QuizSubmitRequestDTO request) {
        QuizSubmitResponseDTO result = quizService.submitAnswer(request);
        return ApiResponseDTO.toResponseEntity(ResultCode.SUCCESS, "퀴즈 채점 성공", result);
    }
    
}
