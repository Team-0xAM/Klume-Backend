package com.oxam.klume.organization.controller;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import com.oxam.klume.organization.service.OrganizationNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "organization_notice", description = "공지사항 관련 API")
@RequestMapping("/organizations/{organizationId}/notices")
@RequiredArgsConstructor
@RestController
public class OrganizationNoticeController {
    private final MemberService memberService;
    private final OrganizationNoticeService organizationNoticeService;

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<List<OrganizationNoticeResponse>> getNotices(@PathVariable final int organizationId) {
        List<OrganizationNoticeResponse> notices = organizationNoticeService.getNotices(organizationId);

        return ResponseEntity.ok(notices);
    }

    @Operation(summary = "공지사항 세부 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<OrganizationNoticeResponse> getNoticeDetail(
            @PathVariable final int organizationId,
            @PathVariable final int noticeId) {
        OrganizationNoticeResponse notice = organizationNoticeService.getNoticeDetail(organizationId, noticeId);
        return ResponseEntity.ok(notice);
    }

    @Operation(summary = "공지사항 등록")
    @PostMapping
    public ResponseEntity<OrganizationNoticeResponse> createNotice(final Authentication authentication,
                                                                   @PathVariable final int organizationId,
                                                                   @Valid @RequestBody final OrganizationNoticeRequest request
    ) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        OrganizationNoticeResponse response =
                organizationNoticeService.createNotice(request, organizationId, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<OrganizationNoticeResponse> updateNotice(final Authentication authentication,
                                                                   @PathVariable final int organizationId,
                                                                   @PathVariable final int noticeId,
                                                                   @Valid @RequestBody OrganizationNoticeRequest request
    ) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        OrganizationNoticeResponse response =
                organizationNoticeService.updateNotice(organizationId, noticeId, request, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNotice(final Authentication authentication,
                                               @PathVariable final int organizationId,
                                               @PathVariable final int noticeId
    ) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        organizationNoticeService.deleteNotice(organizationId, noticeId, member);

        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }
}