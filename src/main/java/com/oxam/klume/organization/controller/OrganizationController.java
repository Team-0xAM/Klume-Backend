package com.oxam.klume.organization.controller;

import com.oxam.klume.organization.dto.InviteCodeResponseDTO;
import com.oxam.klume.organization.dto.OrganizationMemberRoleResponseDTO;
import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.dto.OrganizationResponseDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "organization", description = "조직 관련 API")
@RequestMapping("/organizations")
@RequiredArgsConstructor
@RestController
public class OrganizationController {
    private final OrganizationService organizationService;

    @Operation(summary = "조직 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponseDTO> createOrganization(@Parameter(name = "memberId") final int memberId,
                                                                      @RequestPart(value = "image", required = false) final MultipartFile file,
                                                                      @RequestPart("requestDTO") @Valid final OrganizationRequestDTO requestDTO) {
        // TODO 로그인한 회원 ID 가져오기

        final Organization organization = organizationService.createOrganization(memberId, file, requestDTO);

        final OrganizationResponseDTO response = OrganizationResponseDTO.of(organization);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "초대 코드 발급")
    @PostMapping("/{organizationId}/invitations")
    public ResponseEntity<InviteCodeResponseDTO> createInvitationCode(@Parameter(name = "memberId") final int memberId,
                                                                      @PathVariable("organizationId") final int organizationId) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

        final String invitationCode = organizationService.createInvitationCode(organizationId, memberId);

        final InviteCodeResponseDTO inviteCodeResponseDTO = new InviteCodeResponseDTO(invitationCode);

        return ResponseEntity.ok(inviteCodeResponseDTO);
    }

    @Operation(summary = "조직 내 로그인한 회원의 권한 조회")
    @GetMapping("/{organizationId}/role")
    public ResponseEntity<OrganizationMemberRoleResponseDTO> findOrganizationMemberRole(
            @Parameter(name = "memberId") final int memberId,
            @PathVariable("organizationId") final int organizationId) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

        final OrganizationMember organizationMember =
                organizationService.findOrganizationMemberRole(memberId, organizationId);

        final OrganizationMemberRoleResponseDTO response = new OrganizationMemberRoleResponseDTO(organizationMember.getRole());

        return ResponseEntity.ok(response);
    }
}