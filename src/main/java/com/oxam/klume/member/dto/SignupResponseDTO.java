package com.oxam.klume.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class SignupResponseDTO {

    @Schema(description = "회원 ID", example = "1")
    private int id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "가입일시", example = "2025-11-07 14:30:00")
    private String createdAt;
}
