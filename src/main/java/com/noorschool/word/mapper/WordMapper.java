package com.noorschool.word.mapper;

import com.noorschool.quiz.model.vo.QuizWordVO;
import com.noorschool.word.model.vo.WordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WordMapper {

    List<QuizWordVO> selectRandomWords(@Param("count") int count);

    List<WordVO> selectRandomWrongWords(@Param("wordId") Long wordId,
                                        @Param("count") int count);
    
    WordVO selectWordById(@Param("wordId") Long wordId);

    List<WordVO> selectActiveWords();
}