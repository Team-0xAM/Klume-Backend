package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationMemberInfoResponseDTO {
    @Schema(description = "조직 멤버 ID", example = "1")
    private int organizationMemberId;

    @Schema(description = "조직 내 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "조직 권한", example = "MEMBER")
    private OrganizationRole role;

    @Schema(description = "그룹 ID", example = "1")
    private Integer groupId;

    @Schema(description = "그룹 이름", example = "개발팀")
    private String groupName;

    public static OrganizationMemberInfoResponseDTO of(final OrganizationMember organizationMember) {
        return OrganizationMemberInfoResponseDTO.builder()
                .organizationMemberId(organizationMember.getId())
                .nickname(organizationMember.getNickname())
                .role(organizationMember.getRole())
                .groupId(organizationMember.getOrganizationGroup() != null ?
                        organizationMember.getOrganizationGroup().getId() : null)
                .groupName(organizationMember.getOrganizationGroup() != null ?
                        organizationMember.getOrganizationGroup().getName() : null)
                .build();
    }
}
