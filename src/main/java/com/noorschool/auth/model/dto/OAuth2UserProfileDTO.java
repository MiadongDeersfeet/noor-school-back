package com.noorschool.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserProfileDTO {
    private String googleSub;
    private String email;
    private String name;
    private String profileImage;
}
