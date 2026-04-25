package com.noorschool.word.audio.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WordAudioBatchResponseDTO {

    private int totalCount;
    private int createdCount;
    private int skippedCount;
    private int failedCount;
    private List<Long> failedWordIds;
}
