package com.oxam.klume.auth.oauth;

import com.oxam.klume.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 설명. OAuth2 로그인 성공 시 처리하는 핸들러
 *       로그인 성공 후 JWT 토큰 발급 및 프론트엔드로 리다이렉트
* */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.redirect-url}")
    private String redirectBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        log.info("OAuth2 로그인 성공 - email: {}", email);

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email);
        log.info("JWT 토큰 생성 완료");

        // 프론트엔드로 리다이렉트 (토큰을 URL 파라미터로 전달)
        // 실제 프론트엔드 URL로 변경 필요(임시)
        String redirectUrl = String.format("%s?token=%s&email=%s", redirectBaseUrl, accessToken, email);

        log.info("리다이렉트 URL: {}", redirectUrl);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
