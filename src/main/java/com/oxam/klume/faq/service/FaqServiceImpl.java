package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.entity.Faq;
import com.oxam.klume.faq.exception.FaqNotFoundException;
import com.oxam.klume.faq.repository.FaqRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.exception.MemberNotAdminException;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    // FAQ 전체 목록 조회
    @Override
    public List<FaqResponse> getFaqs() {
        return faqRepository.findAll()
                .stream()
                .map(FaqResponse::of)
                .toList();
    }

    // FAQ 상세 조회
    @Override
    public FaqResponse getFaqDetail(final int faqId) {
        final Faq faq = findFaqById(faqId);

        return FaqResponse.of(faq);
    }

    // FAQ 등록
    @Transactional
    @Override
    public FaqResponse createFaq(final FaqRequest request, final Member member) {
        findMemberSystemRole(member.getId());

        final Faq faq = Faq.create(
                request.getTitle(),
                request.getContent(),
                request.getAnswer(),
                member
        );

        final Faq saved = faqRepository.save(faq);

        return FaqResponse.of(saved);
    }

    // FAQ 수정
    @Transactional
    @Override
    public FaqResponse updateFaq(final int faqId, final Member member, final FaqRequest request) {
        findMemberSystemRole(member.getId());

        final Faq faq = findFaqById(faqId);

        faq.update(request.getTitle(), request.getContent(), request.getAnswer(), member);

        return FaqResponse.of(faq);
    }

    // FAQ 삭제
    @Transactional
    @Override
    public void deleteFaq(final int faqId, final Member member) {
        findMemberSystemRole(member.getId());

        final Faq faq = findFaqById(faqId);

        faqRepository.delete(faq);
    }

    // ============================== 공통 메서드 =====================================
    private MemberSystemRole findMemberSystemRole(final int memberId) {
        final MemberSystemRole memberRole = memberSystemRoleRepository.findFirstByMemberId(memberId);
        if (!memberRole.getSystemRole().getName().equals(Role.ADMIN)) {
            throw new MemberNotAdminException("사용자가 시스템 관리자가 아닙니다.");
        }
        return memberRole;
    }

    private Faq findFaqById(final int faqId) {
        final Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new FaqNotFoundException("FAQ 게시물이 존재하지 않습니다."));
        return faq;
    }
}