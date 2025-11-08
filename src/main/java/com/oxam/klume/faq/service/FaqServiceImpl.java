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
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;
    private final MemberRepository memberRepository;
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
        Faq faq = findFaqById(faqId);
        return FaqResponse.of(faq);
    }

    // FAQ 등록
    @Transactional
    @Override
    public FaqResponse createFaq(final FaqRequest request, final int memberId) {
        Member member = findMemberById(memberId);
        findMemberSystemRole(memberId);

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
        Member member = findMemberById(memberId);
        findMemberSystemRole(memberId);
        Faq faq = findFaqById(faqId);

        faq.update(request.getTitle(), request.getContent(), request.getAnswer(), member);

        return FaqResponse.of(faq);
    }

    // FAQ 삭제
    @Transactional
    @Override
    public void deleteFaq(int faqId, int memberId) {
        findMemberById(memberId);
        findMemberSystemRole(memberId);
        Faq faq = findFaqById(faqId);

        faqRepository.delete(faq);
    }


    // ============================== 공통 메서드 =====================================
    private Member findMemberById(final int memberId){
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("사용자가 존재하지 않습니다."));
    }

    private MemberSystemRole findMemberSystemRole(final int memberId) {
        MemberSystemRole memberRole = memberSystemRoleRepository.findByMemberId(memberId);
        if(!memberRole.getSystemRole().getName().equals(Role.ADMIN)){
            throw new MemberNotAdminException("사용자가 시스템 관리자가 아닙니다.");
        }
        return memberRole;
    }

    private Faq findFaqById(int faqId) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new FaqNotFoundException("FAQ 게시물이 존재하지 않습니다."));
        return faq;
    }
}