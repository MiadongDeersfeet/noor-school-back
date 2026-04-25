package com.noorschool.quiz.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResponseDTO {

    private Long wordId; 			// 정답PK
    private String question; 		// 문제
    private List<String> options;   // 보기
    private String category; 		// 카테고리
    private Integer difficulty; 	// 난이도
    private String correctAudioUrl; // 정답 단어 음성 URL (채점 후 자동재생용)
}