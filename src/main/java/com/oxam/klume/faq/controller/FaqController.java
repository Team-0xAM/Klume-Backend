package com.oxam.klume.faq.controller;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.service.FaqService;
import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FAQ", description = "FAQ 관련 API")
@RequestMapping("/faqs")
@RequiredArgsConstructor
@RestController
public class FaqController {
    private final FaqService faqService;

    private final int memberId = 6;  // TODO: 현재 로그인한 사용자 ID 가져오기

    @Operation(
            summary = "FAQ 등록",
            description = "시스템 관리자가 새로운 FAQ 게시물을 등록할 수 있다." )
    @PostMapping
    public ResponseEntity<FaqResponse> createFAQ(@RequestBody final FaqRequest request) {
        FaqResponse response = faqService.createFaq(request, memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "FAQ 수정",
            description = "시스템 관리자는 기존의 FAQ 게시물을 수정할 수 있다." )
    @PutMapping("/{faqId}")
    public ResponseEntity<FaqResponse> updateFAQ( @PathVariable final int faqId,
                                                  @Valid @RequestBody final FaqRequest request
    ) {
        FaqResponse response = faqService.updateFaq(faqId, memberId, request);
        return ResponseEntity.ok(response);
    }
}
