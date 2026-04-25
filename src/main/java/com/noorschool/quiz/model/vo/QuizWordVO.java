package com.noorschool.quiz.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 퀴즈 생성용 단어 조회 VO.
 * TB_WORD + TB_WORD_AUDIO LEFT JOIN 결과를 담는다.
 * WordVO와 달리 audioUrl을 포함하며, QuizResponseDTO의 correctAudioUrl로 변환된다.
 */
@Getter
@Setter
public class QuizWordVO {

    private Long wordId;
    private String wordKo;
    private String wordAr;
    private String pos;
    private Integer difficulty;
    private String category;
    private Integer frequency;
    private String isPremium;
    private String isActive;
    private LocalDateTime createdAt;

    private String audioUrl; // TB_WORD_AUDIO LEFT JOIN 결과 - 채점 후 correctAudioUrl로 사용
}
