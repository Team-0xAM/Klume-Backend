package com.oxam.klume.security;

import com.oxam.klume.auth.oauth.CustomOAuth2UserService;
import com.oxam.klume.auth.oauth.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebSecurity {

    // TODO: 테스트 후 삭제 - OAuth2 관련 의존성 (구글 로그인 불필요 시)
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Swagger에서 POST 테스트 가능하게)
                .csrf(csrf -> csrf.disable())

                // CORS 활성화 (WebConfig의 CORS 설정 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 기반 인증 사용 - 세션 사용 안함 (STATELESS)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 URL은 모두 허용
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()
                        // 메일 인증, 회원가입 등은 로그인 필요 없음
                        .requestMatchers(
                                "/auth/**",
                                "/mail/**",
                                "/oauth2/**",  // TODO: 테스트 후 삭제 - OAuth2 로그인 (구글 로그인 불필요 시)
                                "/test.html",  // TODO: 테스트 후 삭제 - 테스트 페이지
                                "/*.html",     // TODO: 테스트 후 삭제 - 정적 HTML 파일
                                "/css/**",     // TODO: 테스트 후 삭제 - CSS 파일
                                "/js/**",      // TODO: 테스트 후 삭제 - JS 파일
                                "/profile/**",
                                "/ws/**"       // websocket 관련 핸드셰이크 허용
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 인증 실패 시 401 에러 반환
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                        })
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")  // 구글 로그인 시작 URL
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)  // 커스텀 OAuth2 사용자 서비스
                        )
                        .successHandler(oAuth2SuccessHandler)  // 로그인 성공 핸들러
                )

                .logout(logout -> logout
                        .logoutUrl("/auth/logout")  // 로그아웃 URL
                        .logoutSuccessUrl("/test.html")  // 로그아웃 성공 후 리다이렉트
                        .invalidateHttpSession(true)  // 세션 무효화
                        .deleteCookies("JSESSIONID")  // 쿠키 삭제
                        .permitAll()
                )

                // JWT 필터 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:8080", // Swagger UI
                "http://localhost:5173"  // Vue (Vite)
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
