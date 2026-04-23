package com.noorschool.member.mapper;

import com.noorschool.member.model.dto.MemberDTO;
import com.noorschool.member.model.dto.MemberUpsertDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    MemberDTO selectByMemberId(@Param("memberId") Long memberId);

    MemberDTO selectByGoogleSub(@Param("googleSub") String googleSub);

    MemberDTO selectByEmail(@Param("email") String email);

    int insertMember(MemberUpsertDTO memberUpsertDTO);

    int updateMember(MemberUpsertDTO memberUpsertDTO);

    int updateLastLoginAt(@Param("memberId") Long memberId);
}
