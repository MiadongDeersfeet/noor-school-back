package com.noorschool.terms.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TermsDTO {
    private Long termsId;
    private String termsType;
    private String version;
    private String titleAr;
    private String contentAr;
    private String isRequired;
}
