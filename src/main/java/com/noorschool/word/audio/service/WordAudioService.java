package com.noorschool.word.audio.service;

import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;
import com.noorschool.word.audio.mapper.WordAudioMapper;
import com.noorschool.word.audio.model.dto.WordAudioBatchResponseDTO;
import com.noorschool.word.audio.model.vo.WordAudioVO;
import com.noorschool.word.mapper.WordMapper;
import com.noorschool.word.model.vo.WordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordAudioService {

    private static final String LANGUAGE_CODE = "ko-KR";
    private static final String AUDIO_TYPE = "WORD";
    private static final String SPEAKING_RATE = "NORMAL";
    private static final String PROVIDER = "AZURE";
    private static final String FORMAT = "MP3";
    private static final String IS_ACTIVE = "Y";

    private final WordAudioMapper wordAudioMapper;
    private final WordMapper wordMapper;
    private final AzureTtsService azureTtsService;
    private final AudioStorageService audioStorageService;

    @Transactional
    public WordAudioVO generateWordAudio(Long wordId) {
        if (wordId == null) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "wordId가 없습니다.");
        }

        String voiceName = azureTtsService.getVoiceName();
        WordAudioVO existing = findActiveWordAudio(wordId, voiceName);
        if (existing != null) {
            return existing;
        }

        WordVO word = wordMapper.selectWordById(wordId);
        if (word == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "해당 단어를 찾을 수 없습니다.");
        }
        if (word.getWordKo() == null || word.getWordKo().isBlank()) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "한국어 단어 텍스트가 비어 있습니다.");
        }

        byte[] audioBytes = azureTtsService.synthesizeKoreanWord(word.getWordKo());
        AudioStorageService.SavedAudio savedAudio = audioStorageService.saveWordAudio(wordId, audioBytes);

        WordAudioVO wordAudio = new WordAudioVO();
        wordAudio.setWordId(wordId);
        wordAudio.setLanguageCode(LANGUAGE_CODE);
        wordAudio.setVoiceName(voiceName);
        wordAudio.setAudioType(AUDIO_TYPE);
        wordAudio.setSpeakingRate(SPEAKING_RATE);
        wordAudio.setAudioUrl(savedAudio.audioUrl());
        wordAudio.setStorageKey(savedAudio.storageKey());
        wordAudio.setProvider(PROVIDER);
        wordAudio.setFormat(FORMAT);
        wordAudio.setIsActive(IS_ACTIVE);

        wordAudioMapper.insertWordAudio(wordAudio);

        WordAudioVO saved = findActiveWordAudio(wordId, voiceName);
        if (saved == null) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "오디오 메타데이터 저장 후 조회에 실패했습니다.");
        }
        return saved;
    }

    public WordAudioBatchResponseDTO generateAllMissingWordAudios() {
        List<WordVO> words = wordMapper.selectActiveWords();
        int totalCount = words.size();
        int createdCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        List<Long> failedWordIds = new ArrayList<>();

        String voiceName = azureTtsService.getVoiceName();

        for (WordVO word : words) {
            Long wordId = word.getWordId();
            try {
                WordAudioVO existing = findActiveWordAudio(wordId, voiceName);
                if (existing != null) {
                    skippedCount++;
                    continue;
                }
                generateWordAudio(wordId);
                createdCount++;
            } catch (Exception ex) {
                log.warn("단어 음성 생성 실패 wordId={}, message={}", wordId, ex.getMessage());
                failedCount++;
                failedWordIds.add(wordId);
            }
        }

        return WordAudioBatchResponseDTO.builder()
                .totalCount(totalCount)
                .createdCount(createdCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .failedWordIds(failedWordIds)
                .build();
    }

    private WordAudioVO findActiveWordAudio(Long wordId, String voiceName) {
        return wordAudioMapper.selectWordAudio(
                wordId,
                LANGUAGE_CODE,
                AUDIO_TYPE,
                SPEAKING_RATE,
                voiceName
        );
    }
}
