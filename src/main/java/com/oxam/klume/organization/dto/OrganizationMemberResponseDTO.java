package com.oxam.klume.organization.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationMemberResponseDTO {
    @Schema(description = "조직 멤버 id", example = "1")
    private int organizationMemberId;

    @Schema(description = "조직 내 패널티 수", example = "0")
    private int penaltyCount;

    @Schema(description = "조직 내 정지 여부", example = "1")
    private boolean isBanned;

    @Schema(description = "조직 내 정지일자", example = "2025-11-10 10:00:00")
    private String bannedAt;

    @Schema(description = "조직 권한", example = "MEMBER")
    private OrganizationRole organizationRole;

    @Schema(description = "조직 내 닉네임", example = "김환화")
    private String nickname;

    @Schema(description = "조직 id", example = "1")
    private int organizationId;

    @Schema(description = "조직명", example = "한화 시스템 BEYOND SW 캠프")
    private String organizationName;

    @Schema(description = "조직 그룹 id", example = "1")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer organizationGroupId;

    @Schema(description = "조직 그룹명", example = "19기")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String organizationGroupName;

    @Builder
    public OrganizationMemberResponseDTO(final int organizationMemberId, final int penaltyCount, final String bannedAt,
                                         final boolean isBanned, final OrganizationRole organizationRole,
                                         final String nickname, final int organizationId, final String organizationName,
                                         final Integer organizationGroupId, final String organizationGroupName) {
        this.organizationMemberId = organizationMemberId;
        this.penaltyCount = penaltyCount;
        this.bannedAt = bannedAt;
        this.isBanned = isBanned;
        this.organizationRole = organizationRole;
        this.nickname = nickname;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.organizationGroupId = organizationGroupId;
        this.organizationGroupName = organizationGroupName;
    }

    public static OrganizationMemberResponseDTO of(final OrganizationMember organizationMember) {
        return OrganizationMemberResponseDTO.builder()
                .organizationMemberId(organizationMember.getId())
                .penaltyCount(organizationMember.getPenaltyCount())
                .bannedAt(organizationMember.getBannedAt())
                .isBanned(organizationMember.isBanned())
                .organizationRole(organizationMember.getRole())
                .nickname(organizationMember.getNickname())
                .organizationId(organizationMember.getOrganization().getId())
                .organizationName(organizationMember.getOrganization().getName())
                .organizationGroupId((organizationMember.getOrganizationGroup() == null)
                        ? null : organizationMember.getOrganizationGroup().getId())
                .organizationGroupName((organizationMember.getOrganizationGroup() == null)
                        ? null : organizationMember.getOrganizationGroup().getName())
                .build();
    }
}