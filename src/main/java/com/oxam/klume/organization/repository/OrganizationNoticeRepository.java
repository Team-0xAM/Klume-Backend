package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.OrganizationNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationNoticeRepository extends JpaRepository<OrganizationNotice, Integer> {
    List<OrganizationNotice> findByOrganizationId(int organizationId);
    Optional<OrganizationNotice> findByOrganizationIdAndId(int organizationId, int id);
}
