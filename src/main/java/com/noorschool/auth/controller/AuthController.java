package com.noorschool.auth.controller;

import com.noorschool.auth.model.dto.AuthTokenResponseDTO;
import com.noorschool.auth.security.cookie.AuthCookieService;
import com.noorschool.auth.service.OAuth2Service;
import com.noorschool.auth.service.RefreshTokenService;
import com.noorschool.common.model.dto.ApiResponseDTO;
import com.noorschool.common.model.vo.ResultCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthCookieService authCookieService;
    private final OAuth2Service oAuth2Service;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/google/login-url")
    public ResponseEntity<ApiResponseDTO<String>> getGoogleLoginUrl() {
        return ApiResponseDTO.toResponseEntity(
                ResultCode.SUCCESS,
                "구글 로그인 URL 조회 성공",
                "/oauth2/authorization/google"
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<AuthTokenResponseDTO>> refresh(
            HttpServletRequest request
    ) {
        String refreshToken = extractRefreshToken(request);
        AuthTokenResponseDTO tokenResponse = oAuth2Service.reissueByRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        authCookieService.createRefreshCookie(
                                tokenResponse.getRefreshToken(),
                                tokenResponse.getRefreshTokenExpiresIn()
                        ).toString()
                )
                .body(ApiResponseDTO.success(ResultCode.SUCCESS, "토큰 갱신 성공", tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout(
            HttpServletRequest request
    ) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeByToken(refreshToken);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.expireRefreshCookie().toString())
                .body(ApiResponseDTO.success(ResultCode.SUCCESS, "로그아웃 성공", "logged out"));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        String refreshCookieName = authCookieService.getRefreshCookieName();
        for (Cookie cookie : cookies) {
            if (refreshCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
