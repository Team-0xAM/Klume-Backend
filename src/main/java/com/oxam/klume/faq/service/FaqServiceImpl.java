package com.oxam.klume.faq.service;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.entity.Faq;
import com.oxam.klume.faq.repository.FaqRepository;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.entity.SystemRole;
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.exception.MemberNotAdminException;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;
    private final MemberRepository memberRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    @Override
    public FaqResponse createFaq(FaqRequest request, int memberId) {
        Member member = checkMemberAndRole(memberId);

        // faq 생성
        Faq faq = Faq.create(
                request.getTitle(),
                request.getContent(),
                request.getAnswer(),
                member
        );

        Faq saved = faqRepository.save(faq);

        return FaqResponse.of(saved);

    }

    // ============================== 공통 메서드 =====================================
    private Member checkMemberAndRole(final int memberId) {
        // 사용자가 존재하는지 확인
       Member member = memberRepository.findMemberById(memberId)
               .orElseThrow(() -> new MemberNotFoundException("사용자가 존재하지 않습니다."));

        // 사용자가 시스템 관리자 권한이 있는지 확인
        MemberSystemRole memberRole = memberSystemRoleRepository.findByMemberId(memberId);
        if(!memberRole.getSystemRole().equals(Role.ADMIN)){
            throw new MemberNotAdminException("사용자가 시스템 관리자가 아닙니다.");
        }


      return member;
    }
}