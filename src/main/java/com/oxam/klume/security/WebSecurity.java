package com.oxam.klume.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Swagger에서 POST 테스트 가능하게)
                .csrf(csrf -> csrf.disable())

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
                                "/auth/**"
                        ).permitAll()
                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )

                // 로그인 폼 비활성화
                .formLogin(login -> login.disable())

                // 로그아웃 비활성화 (API용이니까)
                .logout(logout -> logout.disable());

        return http.build();
    }
}
