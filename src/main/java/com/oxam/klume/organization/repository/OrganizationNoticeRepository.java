package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.OrganizationNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationNoticeRepository extends JpaRepository<OrganizationNotice, Integer> {
    @Query("SELECT n FROM OrganizationNotice n JOIN FETCH n.organizationMember WHERE n.organization.id = :organizationId")
    List<OrganizationNotice> findByOrganizationId(@Param("organizationId") int organizationId);

    @Query("SELECT n FROM OrganizationNotice n JOIN FETCH n.organizationMember WHERE n.id = :noticeId")
    Optional<OrganizationNotice> findById(@Param("noticeId") int noticeId);

    Optional<OrganizationNotice> findByOrganizationIdAndId(int organizationId, int id);
}
