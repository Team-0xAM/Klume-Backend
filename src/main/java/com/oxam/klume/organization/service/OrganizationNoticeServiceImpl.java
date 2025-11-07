package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.OrganizationNotice;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationNoticeRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganizationNoticeServiceImpl implements OrganizationNoticeService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationNoticeRepository organizationNoticeRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public List<OrganizationNoticeResponse> getNotices(int organizationId) {

        List<OrganizationNotice> notices = organizationNoticeRepository.findByOrganizationId(organizationId);
        List<OrganizationNoticeResponse> responses = notices.stream()
                .map(OrganizationNoticeResponse::of)
                .toList();

        return responses;
    }

    @Override
    public OrganizationNoticeResponse getNoticeDetail(int organizationId, int noticeId) {
        OrganizationNotice notice = organizationNoticeRepository.findByOrganizationIdAndId(organizationId, noticeId)
                .orElseThrow(() -> new RuntimeException("해당 공지사항을 찾을 수 없습니다."));
        return OrganizationNoticeResponse.of(notice);
    }

    @Override
    public OrganizationNoticeResponse createNotice(OrganizationNoticeRequest request, int organizationId, int memberId) {
        // TODO 전역 에러 처리
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("조직이 존재하지 않습니다"));

        OrganizationMember member = organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 가입하지 않은 조직입니다."));

        if(member.getRole() != OrganizationRole.ADMIN){
            throw new IllegalArgumentException("공지사항을 수정할 권한이 없습니다.");
        }

        OrganizationNotice notice = OrganizationNotice.create(
                request.getTitle(),
                request.getContent(),
                organization,
                member
        );

        OrganizationNotice saved = organizationNoticeRepository.save(notice);

        return OrganizationNoticeResponse.of(saved);
    }

    @Override
    public OrganizationNoticeResponse updateNotice(int organizationId, int noticeId, OrganizationNoticeRequest request, int memberId) {
        // TODO 전역 에러 처리
        OrganizationNotice notice = organizationNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        OrganizationMember member = organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 가입하지 않은 조직입니다."));

        if (notice.getOrganization().getId() != organizationId) {
            throw new IllegalArgumentException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        if(member.getRole() != OrganizationRole.ADMIN){
            throw new IllegalArgumentException("공지사항을 수정할 권한이 없습니다.");
        }

        notice.update(request.getTitle(), request.getContent());
        OrganizationNotice saved = organizationNoticeRepository.save(notice);

        return OrganizationNoticeResponse.of(saved);
    }


    @Override
    public void deleteNotice(int organizationId, int noticeId, int memberId) {
        OrganizationNotice notice = organizationNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        OrganizationMember member = organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 가입하지 않은 조직입니다."));

        if(member.getRole() != OrganizationRole.ADMIN){
            throw new IllegalArgumentException("공지사항을 수정할 권한이 없습니다.");
        }

        if (notice.getOrganization().getId() != organizationId) {
            throw new IllegalArgumentException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        organizationNoticeRepository.delete(notice);
    }
}
