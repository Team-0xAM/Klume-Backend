package com.oxam.klume.organization.repository;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
    boolean existsByMemberAndOrganizationAndRole(final Member member, final Organization organization,
                                                 final OrganizationRole role);
}