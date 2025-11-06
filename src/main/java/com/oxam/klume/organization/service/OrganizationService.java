package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {
    void createOrganization(final int memberId, final MultipartFile file, final OrganizationRequestDTO requestDTO);

    String createInviteCode(final int organizationId, final int memberId);
}