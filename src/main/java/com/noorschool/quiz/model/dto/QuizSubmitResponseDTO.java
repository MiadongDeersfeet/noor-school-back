package com.noorschool.quiz.model.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 채점 결과를 사용자에게 전달해주는 DTO
 */
@Getter
@Builder
public class QuizSubmitResponseDTO {

    private Long wordId;
    private String question;
    private String selectedAnswer;
    private String correctAnswer;
    private boolean correct;
}