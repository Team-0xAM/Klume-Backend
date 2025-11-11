package com.oxam.klume.faq.controller;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.service.FaqService;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FAQ", description = "FAQ 관련 API")
@RequestMapping("/faqs")
@RequiredArgsConstructor
@RestController
public class FaqController {
    private final MemberService memberService;
    private final FaqService faqService;

    @Operation(
            summary = "FAQ 전체 목록 조회",
            description = "모든 사용자와 관리자는 FAQ 목록을 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<FaqResponse>> getFaqs() {
        final List<FaqResponse> faqs = faqService.getFaqs();

        return ResponseEntity.ok(faqs);
    }

    @Operation(
            summary = "FAQ 상세 조회",
            description = "모든 사용자와 관리자는 FAQ 게시물을 상세 조회할 수 있다.")
    @GetMapping("/{faqId}")
    public ResponseEntity<FaqResponse> getFaqDetail(@PathVariable final int faqId) {
        final FaqResponse faq = faqService.getFaqDetail(faqId);

        return ResponseEntity.ok(faq);
    }

    @Operation(
            summary = "FAQ 등록",
            description = "시스템 관리자가 새로운 FAQ 게시물을 등록할 수 있다.")
    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(final Authentication authentication,
                                                 @Valid @RequestBody final FaqRequest request) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final FaqResponse response = faqService.createFaq(request, member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "FAQ 수정",
            description = "시스템 관리자는 기존의 FAQ 게시물을 수정할 수 있다.")
    @PutMapping("/{faqId}")
    public ResponseEntity<FaqResponse> updateFaq(final Authentication authentication, @PathVariable final int faqId,
                                                 @Valid @RequestBody final FaqRequest request) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        FaqResponse response = faqService.updateFaq(faqId, member, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "FAQ 삭제",
            description = "시스템 관리자는 FAQ 게시물을 삭제할 수 있다.")
    @DeleteMapping("/{faqId}")
    public ResponseEntity<String> deleteFaq(final Authentication authentication, @PathVariable final int faqId) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        faqService.deleteFaq(faqId, member);

        return ResponseEntity.ok("FAQ 게시물이 삭제되었습니다.");
    }
}