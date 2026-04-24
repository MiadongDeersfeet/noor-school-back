package com.noorschool.terms.mapper;

import com.noorschool.terms.model.dto.TermsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TermsMapper {
    List<TermsDTO> selectActiveTerms();
    void insertAgreement(@Param("memberId") Long memberId,
                         @Param("termsId") Long termsId,
                         @Param("isAgreed") String isAgreed);
}
