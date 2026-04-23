package com.noorschool.member.service;

import com.noorschool.member.mapper.MemberMapper;
import com.noorschool.member.model.dto.MemberDTO;
import com.noorschool.member.model.dto.MemberUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public MemberDTO findById(Long memberId) {
        return memberMapper.selectByMemberId(memberId);
    }

    public MemberDTO findByGoogleSub(String googleSub) {
        return memberMapper.selectByGoogleSub(googleSub);
    }

    public MemberDTO findByEmail(String email) {
        return memberMapper.selectByEmail(email);
    }

    @Transactional
    public Long upsert(MemberUpsertDTO memberUpsertDTO) {
        if (memberUpsertDTO.getMemberId() == null) {
            memberMapper.insertMember(memberUpsertDTO);
            return memberUpsertDTO.getMemberId();
        }

        memberMapper.updateMember(memberUpsertDTO);
        return memberUpsertDTO.getMemberId();
    }

    @Transactional
    public void touchLastLogin(Long memberId) {
        memberMapper.updateLastLoginAt(memberId);
    }
}
