package com.noorschool.quiz.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuizSetResponseDTO {

    private int totalCount;
    private List<QuizResponseDTO> quizzes;
}