package com.oxam.klume.member.controller;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.organization.dto.OrganizationResponseDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "member", description = "회원 관련 API")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final OrganizationService organizationService;

    @Operation(summary = "속한 조직 목록 조회")
    @GetMapping("/my/organizations")
    public ResponseEntity<List<OrganizationResponseDTO>> getMyOrganizations(final Authentication authentication) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final List<Organization> organizations = organizationService.findOrganizationByMember(member);

        final List<OrganizationResponseDTO> responses = organizations.stream()
                .map(OrganizationResponseDTO::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
