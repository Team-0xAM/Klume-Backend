package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequestDTO {
    @NotBlank(message = "조직명을 입력해주세요.")
    @Schema(description = "조직명")
    private String name;

    @NotBlank(message = "조직에 관한 설명을 입력해주세요.")
    @Schema(description = "조직 설명")
    private String description;

    @NotBlank(message = "조직에서 사용할 닉네임을 입력해주세요.")
    @Schema(description = "조직에서 사용할 닉네임")
    private String nickname;
}