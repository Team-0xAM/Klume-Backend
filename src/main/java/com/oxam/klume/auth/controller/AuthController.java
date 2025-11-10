package com.oxam.klume.auth.controller;

import com.oxam.klume.auth.oauth.CustomOAuth2User;
import com.oxam.klume.member.dto.LoginRequestDTO;
import com.oxam.klume.member.dto.LoginResponseDTO;
import com.oxam.klume.member.dto.SignupRequestDTO;
import com.oxam.klume.member.dto.SignupResponseDTO;
import com.oxam.klume.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "회원 인증 API", description = "회원가입, 로그인 등 인증 관련 API")
public class AuthController {

    private final MemberService memberService;

    @Operation(summary = "로컬 회원가입", description = "이메일 인증 완료 후 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO response = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로컬 로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = memberService.login(request);
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

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkAuthStatus(Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            result.put("authenticated", false);
            result.put("email", null);
            result.put("image", null);
            return ResponseEntity.ok(result);
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String image = null;

        if (principal instanceof CustomOAuth2User oAuth2User) {
            email = oAuth2User.getEmail();
            image = oAuth2User.getImageUrl();
        }

        result.put("authenticated", true);
        result.put("email", email);
        result.put("image", image);

        return ResponseEntity.ok(result);
    }


}
