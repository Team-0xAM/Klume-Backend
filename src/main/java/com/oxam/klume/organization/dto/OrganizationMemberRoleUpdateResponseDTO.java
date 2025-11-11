package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.OrganizationMember;
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
public class OrganizationMemberRoleUpdateResponseDTO {
    @Schema(description = "조직 멤버 id", example = "1")
    private int organizationMemberId;

    @Schema(description = "조직 내 권한", example = "MEMBER")
    private OrganizationRole organizationRole;

    public static OrganizationMemberRoleUpdateResponseDTO of(final OrganizationMember organizationMember) {
        return new OrganizationMemberRoleUpdateResponseDTO(organizationMember.getId(), organizationMember.getRole());
    }
}