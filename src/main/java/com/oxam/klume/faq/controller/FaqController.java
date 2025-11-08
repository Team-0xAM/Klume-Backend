package com.oxam.klume.faq.controller;

import com.oxam.klume.faq.dto.FaqRequest;
import com.oxam.klume.faq.dto.FaqResponse;
import com.oxam.klume.faq.service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FAQ", description = "FAQ 관련 API")
@RequestMapping("/faqs")
@RequiredArgsConstructor
@RestController
public class FaqController {
    private final FaqService faqService;

    @Operation(summary = "FAQ 등록")
    @PostMapping
    public ResponseEntity<FaqResponse> createFAQ(@RequestBody FaqRequest request) {
        // TODO: 현재 로그인한 사용자 ID 가져오기
        final int memberId = 6;

        FaqResponse response =
                faqService.createFaq(request, memberId);

        return ResponseEntity.ok(response);
    }

}
