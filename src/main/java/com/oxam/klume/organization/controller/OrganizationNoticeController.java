package com.oxam.klume.organization.controller;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.dto.OrganizationNoticeResponse;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.service.OrganizationNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "organization_notice", description = "공지사항 관련 API")
@RequestMapping("/organizations/{organizationId}/notices")
@RequiredArgsConstructor
@RestController
public class OrganizationNoticeController {
    private final OrganizationNoticeService organizationNoticeService;

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<List<OrganizationNoticeResponse>> getNotices(@PathVariable final int organizationId) {
        List<OrganizationNoticeResponse> notices = organizationNoticeService.getNotices(organizationId);
        return ResponseEntity.ok(notices);
    }


    @Operation(summary = "공지사항 세부 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<OrganizationNoticeResponse> getNotices(
            @PathVariable final int organizationId,
            @PathVariable final int noticeId) {
        OrganizationNoticeResponse notice = organizationNoticeService.getNoticeDetail(organizationId,noticeId);
        return ResponseEntity.ok(notice);
    }

    @Operation(summary = "공지사항 등록")
    @PostMapping
    public ResponseEntity<OrganizationNoticeResponse> createNotice(
            @PathVariable final int organizationId,
            @RequestBody final OrganizationNoticeRequest request
    ) {
        // TODO: 현재 로그인한 사용자 ID 가져오기
        final int memberId = 5;

        OrganizationNoticeResponse response =
                organizationNoticeService.createNotice(request, organizationId, memberId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<OrganizationNoticeResponse> updateNotice(
            @PathVariable final int organizationId,
            @PathVariable final int noticeId,
            @RequestBody OrganizationNoticeRequest request
    ) {
        // TODO 현재 사용자 ID 가져오기
        int memberId = 5;

        organizationNoticeService.updateNotice(organizationId, noticeId, request, memberId);
        OrganizationNoticeResponse response =
                organizationNoticeService.updateNotice(organizationId, noticeId, request, memberId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNotice(
            @PathVariable final int organizationId,
            @PathVariable final int noticeId
    ) {
        // TODO 현재 사용자 ID 가져오기
        int memberId = 5;

        organizationNoticeService.deleteNotice(organizationId, noticeId, memberId);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }



}
