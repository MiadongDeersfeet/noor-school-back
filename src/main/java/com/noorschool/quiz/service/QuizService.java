package com.noorschool.quiz.service;

import com.noorschool.quiz.model.dto.QuizSetResponseDTO;
import com.noorschool.quiz.model.dto.QuizSubmitRequestDTO;
import com.noorschool.quiz.model.dto.QuizSubmitResponseDTO;

public interface QuizService {
	
	/**
	 * 퀴즈 세트 만들기
	 * @param count (퀴즈 한 판에 제공할 문제 개수 -> 5)
	 * @return 퀴즈 세트 DTO
	 */
    QuizSetResponseDTO createRandomQuizSet(int count);
    
    /**
     * 채점하기
     * @param request (사용자가 선택한 정답)
     * @return 채점 결과 DTO
     */
    QuizSubmitResponseDTO submitAnswer(QuizSubmitRequestDTO request);
    
}