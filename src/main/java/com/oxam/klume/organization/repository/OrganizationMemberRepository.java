package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
}
