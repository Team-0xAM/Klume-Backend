package com.oxam.klume.auth.controller;

import com.oxam.klume.member.dto.LoginRequest;
import com.oxam.klume.member.dto.LoginResponse;
import com.oxam.klume.member.dto.SignupRequest;
import com.oxam.klume.member.dto.SignupResponse;
import com.oxam.klume.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "회원 인증 API", description = "회원가입, 로그인 등 인증 관련 API")
public class AuthController {

    private final MemberService memberService;

    @Operation(summary = "로컬 회원가입", description = "이메일 인증 완료 후 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로컬 로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "세션을 무효화하고 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Spring Security의 로그아웃 처리
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("로그아웃되었습니다.");
    }
}
