package com.noorschool.auth.security.handler;

import com.noorschool.auth.model.dto.AuthTokenResponseDTO;
import com.noorschool.auth.model.dto.OAuth2UserProfileDTO;
import com.noorschool.auth.model.vo.OAuth2ProviderType;
import com.noorschool.auth.oauth.userinfo.OAuth2UserInfo;
import com.noorschool.auth.oauth.userinfo.OAuth2UserInfoFactory;
import com.noorschool.auth.security.cookie.AuthCookieService;
import com.noorschool.auth.service.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2Service oAuth2Service;
    private final AuthCookieService authCookieService;

    @Value("${app.auth.oauth2.success-redirect-uri}")
    private String successRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(OAuth2ProviderType.GOOGLE, oAuth2User.getAttributes());

        AuthTokenResponseDTO tokenResponse = oAuth2Service.loginWithGoogleProfile(
                OAuth2UserProfileDTO.builder()
                        .googleSub(userInfo.getProviderId())
                        .email(userInfo.getEmail())
                        .name(userInfo.getName())
                        .profileImage(userInfo.getProfileImage())
                        .build()
        );

        String redirectUrl;

        if (tokenResponse.isNewMember()) {
            // 신규 회원: 아직 DB에 아무것도 저장되지 않음. signupToken만 전달.
            // 쿠키 미발급 (리프레시 토큰 없음).
            redirectUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                    .queryParam("isNewMember", "true")
                    .queryParam("signupToken", tokenResponse.getSignupToken())
                    .encode()
                    .build()
                    .toUriString();
        } else {
            // 기존 회원: 리프레시 쿠키 발급 후 토큰 정보 전달.
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    authCookieService.createRefreshCookie(
                            tokenResponse.getRefreshToken(),
                            tokenResponse.getRefreshTokenExpiresIn()
                    ).toString()
            );
            redirectUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                    .queryParam("accessToken", tokenResponse.getAccessToken())
                    .queryParam("memberId", tokenResponse.getMemberId())
                    .queryParam("email", tokenResponse.getEmail())
                    .queryParam("name", tokenResponse.getName())
                    .queryParam("role", tokenResponse.getRole())
                    .queryParam("isNewMember", "false")
                    .encode()
                    .build()
                    .toUriString();
        }

        response.sendRedirect(redirectUrl);
    }
}
