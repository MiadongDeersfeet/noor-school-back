package com.noorschool.auth.oauth.userinfo;

import com.noorschool.auth.model.vo.OAuth2ProviderType;
import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;

import java.util.Map;

public class OAuth2UserInfoFactory {
    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo create(OAuth2ProviderType providerType, Map<String, Object> attributes) {
        if (providerType == OAuth2ProviderType.GOOGLE) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new BusinessException(ResultCode.INVALID_REQUEST, "지원하지 않는 OAuth2 제공자입니다.");
    }
}
