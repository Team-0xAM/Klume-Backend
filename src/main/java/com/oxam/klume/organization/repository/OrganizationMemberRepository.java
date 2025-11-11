package com.oxam.klume.organization.repository;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Integer> {
    boolean existsByMemberIdAndOrganizationAndRole(final int memberId, final Organization organization,
                                                   final OrganizationRole role);

    Optional<OrganizationMember> findByMemberIdAndOrganization(final int memberId, final Organization organization);

    Optional<OrganizationMember> findByOrganizationIdAndMemberId(final int organizationId, final int memberId);

    int countByOrganizationAndOrganizationGroup(final Organization organization, final OrganizationGroup organizationGroup);

    @Query("SELECT om.organization FROM OrganizationMember om WHERE om.member = :member")
    List<Organization> findOrganizationByMember(@Param("member") final Member member);

    @Modifying
    @Query("UPDATE OrganizationMember om SET om.organizationGroup = :newOrganizationGroup WHERE om.organizationGroup = :oldOrganizationGroup")
    void updateOrganizationGroup(@Param("newOrganizationGroup") final OrganizationGroup newOrganizationGroup,
                                 @Param("oldOrganizationGroup") final OrganizationGroup oldOrganizationGroup);

    int countByOrganizationAndRole(final Organization organization, final OrganizationRole organizationRole);
}