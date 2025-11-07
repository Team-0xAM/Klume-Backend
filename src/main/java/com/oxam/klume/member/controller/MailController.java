package com.oxam.klume.member.controller;

import com.oxam.klume.member.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/mail")
@Tag(name = "메일 인증 API", description = "회원가입 전 이메일 인증 관련 API")
public class MailController {

    private final MailService mailService;

    @Operation(summary = "인증코드 전송", description = "입력한 이메일로 6자리 인증코드를 발송합니다. (3분 유효)")
    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam String email) {
        mailService.sendVerificationCode(email);
        return ResponseEntity.ok("인증코드가 전송되었습니다.");
    }

    @Operation(summary = "인증코드 검증", description = "이메일로 받은 코드를 입력하여 인증을 완료합니다.")
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String email, @RequestParam String code) {
        boolean success = mailService.verifyCode(email, code);
        if (success) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증코드가 일치하지 않습니다.");
        }
    }
}
