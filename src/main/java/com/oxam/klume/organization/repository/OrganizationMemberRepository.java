package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;

import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
    Optional<OrganizationMember> findByOrganizationIdAndMemberId(int organizationId, int memberId);

    boolean existsByMemberIdAndOrganizationAndRole(int memberId, Organization organization, OrganizationRole role);

    Optional<OrganizationMember> findByMemberIdAndOrganization(final int memberId, final Organization organization);

    int countByOrganizationAndOrganizationGroup(final Organization organization, final OrganizationGroup organizationGroup);
}
