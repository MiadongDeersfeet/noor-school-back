package com.noorschool.member.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpsertDTO {
    private Long memberId;
    private String googleSub;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private String role;
    private String status;
}
