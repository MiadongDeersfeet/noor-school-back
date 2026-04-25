package com.noorschool.word.audio.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordAudioVO {
    private Long audioId;
    private Long wordId;
    private String languageCode;
    private String voiceName;
    private String audioType;
    private String speakingRate;
    private String audioUrl;
    private String storageKey;
    private String provider;
    private String format;
    private String isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
