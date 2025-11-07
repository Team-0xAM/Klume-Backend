package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
    Optional<OrganizationMember> findByOrganizationIdAndMemberId(int organizationId, int memberId);
}
