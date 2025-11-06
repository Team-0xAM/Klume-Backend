package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;

public interface OrganizationNoticeService {
    void createNotice(OrganizationNoticeRequest request, int organizationId, int memberId);
}
