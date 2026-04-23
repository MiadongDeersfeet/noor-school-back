package com.noorschool.auth.oauth.userinfo;

public interface OAuth2UserInfo {
    String getProviderId();

    String getEmail();

    String getName();

    String getProfileImage();
}
