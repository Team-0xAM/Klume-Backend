package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationMemberRoleRequestDTO {
    @NotNull(message = "조직 내 권한을 선택해주세요.")
    @Schema(description = "조직 내 권한", defaultValue = "MEMBER")
    OrganizationRole organizationRole;
}