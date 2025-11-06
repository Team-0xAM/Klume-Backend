package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.OrganizationNotice;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationNoticeRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class OrganizationNoticeServiceImpl implements OrganizationNoticeService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationNoticeRepository organizationNoticeRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public void createNotice(OrganizationNoticeRequest request, int organizationId, int memberId) {
        // TODO 전역 에러 처리
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("조직이 존재하지 않습니다"));

        OrganizationMember member = organizationMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        OrganizationNotice notice = OrganizationNotice.create(
                request.getTitle(),
                request.getContent(),
                now,
                null,
                organization,
                member
        );

        organizationNoticeRepository.save(notice);
    }

    @Override
    public void updateNotice(int organizationId, int noticeId, OrganizationNoticeRequest request, int memberId) {
        // TODO 전역 에러 처리
        OrganizationNotice notice = organizationNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (notice.getOrganization().getId() != organizationId) {
            throw new IllegalArgumentException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        // TODO 관리자 권한 확인?

        notice.update(request.getTitle(), request.getContent());
        organizationNoticeRepository.save(notice);
    }


    @Override
    public void deleteNotice(int organizationId, int noticeId, int memberId) {
        OrganizationNotice notice = organizationNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        // TODO 관리자 권한 확인?

        if (notice.getOrganization().getId() != organizationId) {
            throw new IllegalArgumentException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        organizationNoticeRepository.delete(notice);
    }
}
