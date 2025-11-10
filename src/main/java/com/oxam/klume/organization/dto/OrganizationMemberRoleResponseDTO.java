package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationMemberRoleResponseDTO {
    @Schema(description = "조직 권한", example = "MEMBER")
    private OrganizationRole role;
}