package com.noorschool.member.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberDTO {
    private Long memberId;
    private String googleSub;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private String role;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
