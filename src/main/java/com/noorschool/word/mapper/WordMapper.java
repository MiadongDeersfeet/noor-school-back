package com.noorschool.word.mapper;

import com.noorschool.word.model.vo.WordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WordMapper {

    List<WordVO> selectRandomWords(@Param("count") int count);

    List<WordVO> selectRandomWrongWords(@Param("wordId") Long wordId,
                                        @Param("count") int count);
    
    WordVO selectWordById(@Param("wordId") Long wordId);
}