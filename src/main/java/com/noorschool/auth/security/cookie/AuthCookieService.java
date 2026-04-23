package com.noorschool.auth.security.cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieService {
    private final String refreshCookieName;
    private final boolean secure;
    private final String sameSite;
    private final String domain;

    public AuthCookieService(
            @Value("${app.auth.cookie.refresh-cookie-name:NS_REFRESH_TOKEN}") String refreshCookieName,
            @Value("${app.auth.cookie.secure:false}") boolean secure,
            @Value("${app.auth.cookie.same-site:Lax}") String sameSite,
            @Value("${app.auth.cookie.domain:}") String domain
    ) {
        this.refreshCookieName = refreshCookieName;
        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain == null ? "" : domain.trim();
    }

    public String getRefreshCookieName() {
        return refreshCookieName;
    }

    public ResponseCookie createRefreshCookie(String refreshToken, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite);
        if (!domain.isBlank()) {
            builder.domain(domain);
        }
        return builder.build();
    }

    public ResponseCookie expireRefreshCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite);
        if (!domain.isBlank()) {
            builder.domain(domain);
        }
        return builder.build();
    }
}
