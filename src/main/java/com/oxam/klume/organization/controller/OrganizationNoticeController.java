package com.oxam.klume.organization.controller;

import com.oxam.klume.organization.dto.OrganizationNoticeRequest;
import com.oxam.klume.organization.entity.OrganizationNotice;
import com.oxam.klume.organization.repository.OrganizationNoticeRepository;
import com.oxam.klume.organization.service.OrganizationNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "organization_notice", description = "공지사항 관련 API")
@RequestMapping("/organizations/{organizationId}/notices")
@RequiredArgsConstructor
@RestController
public class OrganizationNoticeController {
    private final OrganizationNoticeService organizationNoticeService;

    private final OrganizationNoticeRepository organizationNoticeRepository;

    @Operation(summary = "공지사항 등록")
    @PostMapping("/")
    public ResponseEntity<String> createNotice(
            @PathVariable int organizationId,
            @RequestBody OrganizationNoticeRequest request
    ) {
        // TODO: 현재 로그인한 사용자 ID 가져오기
        final int memberId = 1;

        // TODO: 사용자가 현재 조직의 관리자인지 확인

        organizationNoticeService.createNotice(request, organizationId, memberId);

        return ResponseEntity.status(201).body("공지사항이 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "공지사항 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<String> updateNotice(
            @PathVariable int organizationId,
            @PathVariable int noticeId,
            @RequestBody OrganizationNoticeRequest request
    ) {
        // TODO 현재 사용자 ID 가져오기
        int memberId = 1;

        organizationNoticeService.updateNotice(organizationId, noticeId, request, memberId);
        return ResponseEntity.ok("공지사항이 수정되었습니다.");
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNotice(
            @PathVariable int organizationId,
            @PathVariable int noticeId
    ) {
        // TODO 현재 사용자 ID 가져오기
        int memberId = 1;

        organizationNoticeService.deleteNotice(organizationId, noticeId, memberId);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }



}
