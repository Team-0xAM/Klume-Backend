package com.oxam.klume.organization.service;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;

import java.util.List;

public interface OrganizationNoticeService {
    OrganizationNoticeResponse createNotice(final OrganizationNoticeRequest request, final int organizationId,
                                            final Member member);

    OrganizationNoticeResponse updateNotice(final int organizationId, final int noticeId,
                                            final OrganizationNoticeRequest request, final Member member);

    void deleteNotice(final int organizationId, final int noticeId, final Member member);

    List<OrganizationNoticeResponse> getNotices(final int organizationId);

    OrganizationNoticeResponse getNoticeDetail(final int organizationId, final int noticeId);
}
