package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;

import java.util.List;

public interface OrganizationNoticeService {
    OrganizationNoticeResponse createNotice(final OrganizationNoticeRequest request, final int organizationId, final int memberId);
    OrganizationNoticeResponse updateNotice(final int organizationId, final int noticeId, OrganizationNoticeRequest request, final int memberId);
    void deleteNotice(final int organizationId, final int noticeId, final int memberId);
    List<OrganizationNoticeResponse> getNotices(final int organizationId);
    OrganizationNoticeResponse getNoticeDetail(final int organizationId, final int noticeId);
}
