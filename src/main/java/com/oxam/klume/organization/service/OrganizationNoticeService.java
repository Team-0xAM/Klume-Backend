package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;

import java.util.List;

public interface OrganizationNoticeService {
    void createNotice(OrganizationNoticeRequest request, int organizationId, int memberId);
    void updateNotice(int organizationId, int noticeId, OrganizationNoticeRequest request, int memberId);
    void deleteNotice(int organizationId, int noticeId, int memberId);
    List<OrganizationNoticeResponse> getNotices(int organizationId);
    OrganizationNoticeResponse getNoticeDetail(int organizationId, int noticeId);
}
