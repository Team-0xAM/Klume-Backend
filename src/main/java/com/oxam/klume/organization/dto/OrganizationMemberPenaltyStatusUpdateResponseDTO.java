package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.OrganizationMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationMemberPenaltyStatusUpdateResponseDTO {
    private int organizationMemberId;
    private int penaltyCount;
    private boolean isBanned;
    private String bannedAt;
    private String nickname;

    public static OrganizationMemberPenaltyStatusUpdateResponseDTO of(final OrganizationMember organizationMember) {
        return new OrganizationMemberPenaltyStatusUpdateResponseDTO(organizationMember.getId(),
                organizationMember.getPenaltyCount(), organizationMember.isBanned(),
                organizationMember.getBannedAt(), organizationMember.getNickname());
    }
}