package com.noorschool.quiz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.noorschool.quiz.model.dto.QuizResponseDTO;
import com.noorschool.quiz.model.dto.QuizSetResponseDTO;
import com.noorschool.quiz.model.dto.QuizSubmitRequestDTO;
import com.noorschool.quiz.model.dto.QuizSubmitResponseDTO;
import com.noorschool.word.mapper.WordMapper;
import com.noorschool.word.model.vo.WordVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final WordMapper wordMapper;

    /**
     * 랜덤 퀴즈 한 세트 생성
     *
     * 흐름
     * 1. DB에서 랜덤 단어 count개 조회
     * 2. 각 단어마다 보기 3개 생성 (정답 1 + 오답 2)
     * 3. QuizResponseDTO 리스트로 변환
     * 4. QuizSetResponseDTO로 감싸서 반환
     *
     * @param count 생성할 문제 수 (예: 5)
     * @return QuizSetResponseDTO(퀴즈세트 데이터)
     */
    @Override
    public QuizSetResponseDTO createRandomQuizSet(int count) {

        // 방어 코드: 문제 수는 1 이상이어야 함
        if (count <= 0) {
            throw new IllegalArgumentException("문제 수는 1 이상이어야 합니다.");
        }

        // 1. 랜덤 단어 조회 (정답 후보)
        List<WordVO> answerWords = wordMapper.selectRandomWords(count);

        // 단어가 없을 경우 빈 응답 반환
        if (answerWords == null || answerWords.isEmpty()) {
            return QuizSetResponseDTO.builder()
                    .totalCount(0)
                    .quizzes(new ArrayList<>())
                    .build();
        }

        // 요청한 개수보다 적게 조회된 경우 예외 처리
        if (answerWords.size() < count) {
            throw new IllegalStateException("퀴즈 생성에 필요한 단어 수가 부족합니다.");
        }

        List<QuizResponseDTO> quizzes = new ArrayList<>();

        // 2. 각 단어를 하나의 문제로 변환
        for (WordVO answerWord : answerWords) {

            // 보기 3개 생성 (정답 + 오답 2개)
            List<String> options = createOptions(answerWord);

            // QuizResponseDTO 생성
            QuizResponseDTO quiz = QuizResponseDTO.builder()
                    .wordId(answerWord.getWordId())
                    .question(answerWord.getWordAr())
                    .options(options)
                    .category(answerWord.getCategory())
                    .difficulty(answerWord.getDifficulty())
                    .build();

            quizzes.add(quiz);
        }

        // 3. 퀴즈 리스트를 하나의 세트로 묶어서 반환
        return QuizSetResponseDTO.builder()
                .totalCount(quizzes.size())
                .quizzes(quizzes)
                .build();
    }

    /**
     * 보기 3개 생성 메서드
     *
     * 구성
     * - 정답 1개
     * - 오답 2개
     * - 마지막에 순서 섞기
     *
     * @param answerWord 정답 단어
     * @return 섞인 보기 리스트
     */
    private List<String> createOptions(WordVO answerWord) {

        List<String> options = new ArrayList<>();

        // 1. 정답 추가
        options.add(answerWord.getWordKo());

        // 2. 오답 후보를 넉넉하게 조회 (예: 10개)
        List<WordVO> wrongWords = wordMapper.selectRandomWrongWords(
                answerWord.getWordId(), 10
        );

        // null 방어
        if (wrongWords == null) {
            wrongWords = Collections.emptyList();
        }

        // 중복 방지를 위한 Set
        Set<String> wrongOptionSet = new HashSet<>();

        // 3. 오답 2개만 선택
        for (WordVO wrongWord : wrongWords) {

            // 오답 2개 채우면 종료
            if (wrongOptionSet.size() == 2) {
                break;
            }

            String wrongAnswer = wrongWord.getWordKo();

            // 정답과 동일한 경우 제외
            if (wrongAnswer.equals(answerWord.getWordKo())) {
                continue;
            }

            wrongOptionSet.add(wrongAnswer);
        }

        // 4. 오답 추가
        options.addAll(wrongOptionSet);

        // 보기 3개가 안 만들어지면 예외
        if (options.size() < 3) {
            throw new IllegalStateException("3지선다 구성을 위한 오답 보기가 부족합니다.");
        }

        // 5. 보기 순서 랜덤 셔플
        Collections.shuffle(options);

        return options;
    }
    
    @Override
    public QuizSubmitResponseDTO submitAnswer(QuizSubmitRequestDTO request) {

        // 1. 요청 객체 자체가 null인지 검증
        // 프론트에서 아예 데이터를 안 보내거나 바인딩 실패한 경우 방어
        if (request == null) {
            throw new IllegalArgumentException("요청값이 없습니다.");
        }

        // 2. 문제 식별자(wordId) 검증
        // 어떤 문제를 채점해야 하는지 알아야 하므로 필수값
        if (request.getWordId() == null) {
            throw new IllegalArgumentException("문제 ID가 없습니다.");
        }

        // 3. 사용자가 선택한 답 검증
        // 공백 또는 null이면 채점 불가
        if (request.getSelectedAnswer() == null || request.getSelectedAnswer().isBlank()) {
            throw new IllegalArgumentException("선택한 답이 없습니다.");
        }

        // 4. DB에서 해당 wordId에 대한 단어 조회 (정답 데이터 확보)
        // 이 값이 "진짜 정답" 기준이 됨
        WordVO word = wordMapper.selectWordById(request.getWordId());

        // 5. DB에 해당 문제가 없는 경우 예외 처리
        // 잘못된 wordId로 요청한 경우 방어
        if (word == null) {
            throw new IllegalStateException("해당 문제를 찾을 수 없습니다.");
        }

        // 6. 정답 여부 판단
        // 사용자가 선택한 답과 DB의 정답(wordKo)을 비교
        // equals 사용 → 문자열 정확히 일치해야 정답 처리
        boolean isCorrect = request.getSelectedAnswer().equals(word.getWordKo());

        // 7. 채점 결과를 DTO로 만들어서 반환
        // 프론트는 이 응답을 보고 "정답/오답" UI 처리
        return QuizSubmitResponseDTO.builder()
                .wordId(word.getWordId())              // 문제 ID
                .question(word.getWordAr())            // 문제 (아랍어)
                .selectedAnswer(request.getSelectedAnswer()) // 사용자가 고른 답
                .correctAnswer(word.getWordKo())       // 실제 정답 (채점 결과 표시용)
                .correct(isCorrect)                    // 정답 여부 (true/false)
                .build();
    }
    
}