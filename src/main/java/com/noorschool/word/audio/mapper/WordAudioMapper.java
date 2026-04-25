package com.noorschool.word.audio.mapper;

import com.noorschool.word.audio.model.vo.WordAudioVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WordAudioMapper {

    WordAudioVO selectWordAudio(
            @Param("wordId") Long wordId,
            @Param("languageCode") String languageCode,
            @Param("audioType") String audioType,
            @Param("speakingRate") String speakingRate,
            @Param("voiceName") String voiceName
    );

    int insertWordAudio(WordAudioVO wordAudio);

    int deactivateWordAudio(@Param("wordId") Long wordId);
}
