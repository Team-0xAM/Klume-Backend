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
public class OrganizationMemberRequestDTO {
    @NotBlank(message = "조직 내 닉네임을 입력해주세요.")
    @Schema(description = "조직 내 닉네임", defaultValue = "김한화")
    private String nickname;

    @Schema(description = "조직 그룹 id", defaultValue = "1")
    private Integer organizationGroupId;
}