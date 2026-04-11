package com.noorschool.word.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WordVO {

    private Long wordId;        // WORD_ID (PK)

    private String wordKo;      // WORD_KO (한국어)
    private String wordAr;      // WORD_AR (아랍어)

    private String pos;         // 품사 (명사, 동사 등)

    private Integer difficulty; // 난이도 (1~4)

    private String category;    // 카테고리

    private Integer frequency;  // 빈도수 (선택)

    private String isPremium;   // 프리미엄 여부 (Y/N)
    private String isActive;    // 활성화 여부 (Y/N)

    private LocalDateTime createdAt; // 생성일
}