package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
    boolean existsByMemberIdAndOrganizationAndRole(final int memberId, final Organization organization,
                                                   final OrganizationRole role);

    Optional<OrganizationMember> findByMemberIdAndOrganization(final int memberId, final Organization organization);

    Optional<OrganizationMember> findByOrganizationIdAndMemberId(final int organizationId, final int memberId);

    int countByOrganizationAndOrganizationGroup(final Organization organization, final OrganizationGroup organizationGroup);
}