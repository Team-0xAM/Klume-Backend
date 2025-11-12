package com.oxam.klume.organization.repository;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationGroupRepository extends JpaRepository<OrganizationGroup, Integer> {
    List<OrganizationGroup> findByOrganization(final Organization organization);

    Optional<OrganizationGroup> findByNameAndOrganization(final String name, final Organization organization);
}