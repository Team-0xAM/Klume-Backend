package com.oxam.klume.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {

    @Schema(description = "이메일 (인증 완료된 이메일)", example = "user@example.com")
    private String email;

    @Schema(description = "비밀번호 (8자 이상)", example = "password123!")
    private String password;
}
