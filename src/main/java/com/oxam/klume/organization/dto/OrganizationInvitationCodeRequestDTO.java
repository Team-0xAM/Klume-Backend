package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInvitationCodeRequestDTO {
    @NotBlank(message = "초대 인증 코드를 입력해주세요.")
    @Schema(description = "초대 인증 코드", defaultValue = "2FkmBg")
    private String code;
}
