package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.OrganizationNotice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationNoticeResponse {
    private int noticeId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private int memberId;

    public static OrganizationNoticeResponse of(final OrganizationNotice notice) {
        return OrganizationNoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .memberId(notice.getOrganizationMember().getId())
                .build();
    }
}
