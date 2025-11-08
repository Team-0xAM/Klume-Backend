package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.entity.Faq;
import com.oxam.klume.faq.exception.FaqNotFoundException;
import com.oxam.klume.faq.repository.FaqRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.entity.SystemRole;
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.exception.MemberNotAdminException;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;
    private final MemberRepository memberRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    // FAQ 등록
    @Transactional
    @Override
    public FaqResponse createFaq(final FaqRequest request, final int memberId) {
        Member member = checkMemberAndRole(memberId);

        Faq faq = Faq.create(
                request.getTitle(),
                request.getContent(),
                request.getAnswer(),
                member
        );

        Faq saved = faqRepository.save(faq);

        return FaqResponse.of(saved);
    }

    // FAQ 수정
    @Transactional
    @Override
    public FaqResponse updateFaq(final int faqId, final int memberId, final FaqRequest request) {
        Member member = checkMemberAndRole(memberId);
        Faq faq = findFaqById(faqId);

        faq.update(request.getTitle(), request.getContent(), request.getAnswer(), member);

        return FaqResponse.of(faq);
    }


    // ============================== 공통 메서드 =====================================
    private Member checkMemberAndRole(final int memberId) {
        // 사용자가 존재하는지 확인
       Member member = memberRepository.findMemberById(memberId)
               .orElseThrow(() -> new MemberNotFoundException("사용자가 존재하지 않습니다."));

        // 사용자 시스템 관리자인지 확인
        MemberSystemRole memberRole = memberSystemRoleRepository.findByMemberId(memberId);
        if(!memberRole.getSystemRole().getName().equals(Role.ADMIN)){
            throw new MemberNotAdminException("사용자가 시스템 관리자가 아닙니다.");
        }
      return member;
    }

    private Faq findFaqById(int faqId) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new FaqNotFoundException("FAQ 게시물이 존재하지 않습니다."));
        return faq;
    }
}