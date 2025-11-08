package com.oxam.klume.organization.service;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.OrganizationNotice;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.exception.OrganizationNoticeNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationNoticeRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganizationNoticeServiceImpl implements OrganizationNoticeService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationNoticeRepository organizationNoticeRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    // 공지사항 목록 조회
    @Override
    public List<OrganizationNoticeResponse> getNotices(final int organizationId) {
        findOrganizationById(organizationId);

        List<OrganizationNotice> notices = organizationNoticeRepository.findByOrganizationId(organizationId);
        List<OrganizationNoticeResponse> responses = notices.stream()
                .map(OrganizationNoticeResponse::of)
                .toList();

        return responses;
    }

    // 공지사항 상세 조회
    @Override
    public OrganizationNoticeResponse getNoticeDetail(final int organizationId, final int noticeId) {
        OrganizationNotice notice = findOrganizationNoticeById(noticeId);
        return OrganizationNoticeResponse.of(notice);
    }

    // 공지사항 등록
    @Override
    public OrganizationNoticeResponse createNotice(final OrganizationNoticeRequest request, final int organizationId, final int memberId) {
        Organization organization = findOrganizationById(organizationId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        if(member.getRole() != OrganizationRole.ADMIN){
            throw new OrganizationNotAdminException("공지사항을 수정할 권한이 없습니다.");
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

    // 공지사항 수정
    @Transactional
    @Override
    public OrganizationNoticeResponse updateNotice(final int organizationId, final int noticeId, OrganizationNoticeRequest request, final int memberId) {
        OrganizationNotice notice = findOrganizationNoticeById(noticeId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);

        if (notice.getOrganization().getId() != organizationId) {
            throw new OrganizationNoticeNotFoundException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        if(member.getRole() != OrganizationRole.ADMIN){
            throw new OrganizationNotAdminException("공지사항을 수정할 권한이 없습니다.");
        }

        notice.update(request.getTitle(), request.getContent());
        return OrganizationNoticeResponse.of(notice);
    }

    // 공지사항 삭제
    @Override
    public void deleteNotice(final int organizationId, final int noticeId, final int memberId) {

        findOrganizationById(organizationId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        OrganizationNotice notice = findOrganizationNoticeById(noticeId);

        if(member.getRole() != OrganizationRole.ADMIN){
            throw new OrganizationNotAdminException("공지사항을 수정할 권한이 없습니다.");
        }

        if (notice.getOrganization().getId() != organizationId) {
            throw new OrganizationNoticeNotFoundException("이 공지사항은 해당 조직에 속하지 않습니다.");
        }

        organizationNoticeRepository.delete(notice);
    }

    // ============================== 공통 메서드 =====================================
    private Organization findOrganizationById(final int organizationId){
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("조직이 존재하지 않습니다"));
    }

    private OrganizationMember findOrganizationMemberById(final int organizationId, final int memberId){
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new OrganizationNotFoundException("사용자가 가입하지 않은 조직입니다."));
    }

    private OrganizationNotice findOrganizationNoticeById(final int noticeId){
        return organizationNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new OrganizationNoticeNotFoundException("공지사항이 존재하지 않습니다."));
    }
}
