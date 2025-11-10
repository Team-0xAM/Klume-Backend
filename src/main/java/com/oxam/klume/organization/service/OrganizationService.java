package com.oxam.klume.organization.service;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.dto.OrganizationGroupResponseDTO;
import com.oxam.klume.organization.dto.OrganizationMemberRequestDTO;
import com.oxam.klume.organization.dto.OrganizationMemberRoleRequestDTO;
import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService {
    Organization createOrganization(final Member member, final MultipartFile file, final OrganizationRequestDTO requestDTO);

    String createInvitationCode(final int organizationId, final int memberId);

    OrganizationMember findOrganizationMemberRole(final int memberId, final int organizationId);

    List<OrganizationGroupResponseDTO> findOrganizationGroups(final int memberId, final int organizationId);

    Organization validateInvitationCode(final int memberId, final String code);

    OrganizationMember createOrganizationMember(final Member member, final int organizationId,
                                                final OrganizationMemberRequestDTO requestDTO);

    List<Organization> findOrganizationByMember(final Member member);

    OrganizationMember updateOrganizationMemberRole(final Member member, final int organizationMemberId,
                                                    final int organizationId, final OrganizationMemberRoleRequestDTO requestDTO);
}