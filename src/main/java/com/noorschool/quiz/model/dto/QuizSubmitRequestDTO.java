package com.noorschool.quiz.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자가 선택한 정답을 서버로 보내는 DTO
 */
@Getter
@Setter
public class QuizSubmitRequestDTO {

    private Long wordId;
    private String selectedAnswer;
}