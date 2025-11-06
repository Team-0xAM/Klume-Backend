package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InviteCodeResponseDTO {
    @Schema(description = "초대 코드", example = "2FkmBg")
    private String code;
}