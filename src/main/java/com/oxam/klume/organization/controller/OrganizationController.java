package com.oxam.klume.organization.controller;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.organization.dto.*;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "organization", description = "조직 관련 API")
@RequestMapping("/organizations")
@RequiredArgsConstructor
@RestController
public class OrganizationController {
    private final MemberService memberService;
    private final OrganizationService organizationService;

    @Operation(summary = "조직 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조직 생성 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrganizationResponseDTO.class)
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "FILE_INVALID_EXTENSION", value = """
                            {
                                "code": "FILE002",
                                "message": "Invalid file extension"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "MEMBER_NOT_FOUND", value = """
                            {
                                "code": "MEMBER001",
                                "message": "Member not found"
                            }
                            """),
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """),
                    @ExampleObject(name = "FILE_NOT_FOUND", value = """
                            {
                                "code": "FILE001",
                                "message": "File not found"
                            }
                            """)
            }))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponseDTO> createOrganization(final Authentication authentication,
                                                                      @RequestPart(value = "image", required = false) final MultipartFile file,
                                                                      @RequestPart("requestDTO") @Valid final OrganizationRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final Organization organization = organizationService.createOrganization(member, file, requestDTO);

        final OrganizationResponseDTO response = OrganizationResponseDTO.of(organization);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "초대 코드 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대 코드 발급 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrganizationInvitationCodeResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_MEMBER_ACCESS_DENIED", value = """
                            {
                                "code": "ORGANIZATION003",
                                "message": "Not a member of the organization"
                            }
                            """),
                    @ExampleObject(name = "ORGANIZATION_NOT_ADMIN", value = """
                            {
                                "code": "ORGANIZATION002",
                                "message": "Organization not admin"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """)
            }))
    })
    @PostMapping("/{organizationId}/invitations")
    public ResponseEntity<OrganizationInvitationCodeResponseDTO> createInvitationCode(final Authentication authentication,
                                                                                      @PathVariable("organizationId") final int organizationId) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        final String invitationCode = organizationService.createInvitationCode(organizationId, memberId);

        final OrganizationInvitationCodeResponseDTO response = new OrganizationInvitationCodeResponseDTO(invitationCode);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "조직 내 로그인한 회원의 권한 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조직 내 로그인한 회원의 권한 조회", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrganizationMemberRoleResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_MEMBER_ACCESS_DENIED", value = """
                            {
                                "code": "ORGANIZATION003",
                                "message": "Not a member of the organization"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """)
            }))
    })
    @GetMapping("/{organizationId}/role")
    public ResponseEntity<OrganizationMemberRoleResponseDTO> findOrganizationMemberRole(final Authentication authentication,
                                                                                        @PathVariable("organizationId") final int organizationId) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        final OrganizationMember organizationMember =
                organizationService.findOrganizationMemberRole(memberId, organizationId);

        final OrganizationMemberRoleResponseDTO response = new OrganizationMemberRoleResponseDTO(organizationMember.getRole());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "조직 내 그룹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조직 내 그룹 목록 조회", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = OrganizationGroupResponseDTO.class)
                    )
            )),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """)
            }))
    })
    @GetMapping("/{organizationId}/groups")
    public ResponseEntity<List<OrganizationGroupResponseDTO>> findOrganizationGroups(final Authentication authentication,
                                                                                     @PathVariable("organizationId") final int organizationId) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        final List<OrganizationGroupResponseDTO> response = organizationService.findOrganizationGroups(memberId, organizationId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "초대 코드 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대 코드 검증 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrganizationResponseDTO.class)
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_INVITATION_CODE_INVALID", value = """
                            {
                                "code": "ORGANIZATION005",
                                "message": "Organization invitation code is expired or invalid"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_MEMBER_ALREADY_EXISTS", value = """
                            {
                                "code": "ORGANIZATION004",
                                "message": "Organization member already exists"
                            }
                            """)
            }))
    })
    @PostMapping("/invitations/validation")
    public ResponseEntity<OrganizationResponseDTO> validateInvitationCode(final Authentication authentication,
                                                                          @RequestBody @Valid final OrganizationInvitationCodeRequestDTO requestDTO) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        final Organization organization = organizationService.validateInvitationCode(memberId, requestDTO.getCode());

        final OrganizationResponseDTO response = OrganizationResponseDTO.of(organization);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "조직 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조직 가입 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrganizationMemberResponseDTO.class)
            )),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_INVITATION_CODE_INVALID", value = """
                            {
                                "code": "ORGANIZATION005",
                                "message": "Organization invitation code is expired or invalid"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "MEMBER_NOT_FOUND", value = """
                            {
                                "code": "MEMBER001",
                                "message": "Member not found"
                            }
                            """),
                    @ExampleObject(name = "ORGANIZATION_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION001",
                                "message": "Organization not found"
                            }
                            """),
                    @ExampleObject(name = "ORGANIZATION_GROUP_NOT_FOUND", value = """
                            {
                                "code": "ORGANIZATION006",
                                "message": "Organization group not found"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "ORGANIZATION_MEMBER_ALREADY_EXISTS", value = """
                            {
                                "code": "ORGANIZATION004",
                                "message": "Organization member already exists"
                            }
                            """)
            }))
    })
    @PostMapping("/{organizationId}")
    public ResponseEntity<OrganizationMemberResponseDTO> createOrganizationMember(final Authentication authentication,
                                                                                  @PathVariable("organizationId") final int organizationId,
                                                                                  @RequestBody @Valid final OrganizationMemberRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final OrganizationMember organizationMember = organizationService.createOrganizationMember(member, organizationId, requestDTO);

        final OrganizationMemberResponseDTO response = OrganizationMemberResponseDTO.of(organizationMember);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "권한 변경")
    @PostMapping("{organizationId}/members/{organizationMemberId}/role")
    public ResponseEntity<OrganizationMemberRoleUpdateResponseDTO> updateOrganizationMemberRole(final Authentication authentication,
                                                                                                @PathVariable(name = "organizationMemberId") final int organizationMemberId,
                                                                                                @PathVariable("organizationId") final int organizationId,
                                                                                                @RequestBody @Valid final OrganizationMemberRoleRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final OrganizationMember organizationMember = organizationService.updateOrganizationMemberRole(member, organizationMemberId, organizationId, requestDTO);

        return ResponseEntity.ok(OrganizationMemberRoleUpdateResponseDTO.of(organizationMember));
    }

    @Operation(summary = "그룹 생성")
    @PostMapping("{organizationId}/groups")
    public ResponseEntity<OrganizationGroupResponseDTO> createOrganizationGroup(final Authentication authentication,
                                                                                @PathVariable(name = "organizationId") final int organizationId,
                                                                                @RequestBody @Valid final OrganizationGroupRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final OrganizationGroup organizationGroup =
                organizationService.createOrganizationGroup(member, organizationId, OrganizationGroupRequestDTO.toEntity(requestDTO));

        return ResponseEntity.ok(OrganizationGroupResponseDTO.of(organizationGroup, null));
    }

    @Operation(summary = "그룹 수정")
    @PutMapping("/{organizationId}/groups/{organizationGroupId}")
    public ResponseEntity<OrganizationGroupResponseDTO> updateOrganizationGroup(final Authentication authentication,
                                                                                @PathVariable(name = "organizationId") final int organizationId,
                                                                                @PathVariable(name = "organizationGroupId") final int organizationGroupId,
                                                                                @RequestBody @Valid final OrganizationGroupRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final OrganizationGroup organizationGroup =
                organizationService.updateOrganizationGroup(member, organizationId, organizationGroupId,
                        OrganizationGroupRequestDTO.toEntity(requestDTO));

        return ResponseEntity.ok(OrganizationGroupResponseDTO.of(organizationGroup, null));
    }

    @Operation(summary = "그룹 삭제")
    @DeleteMapping("/{organizationId}/groups/{organizationGroupId}")
    public ResponseEntity<?> deleteOrganizationGroup(final Authentication authentication,
                                                     @PathVariable(name = "organizationId") final int organizationId,
                                                     @PathVariable(name = "organizationGroupId") final int organizationGroupId) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        organizationService.deleteOrganizationGroup(member, organizationId, organizationGroupId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "조직 정보 수정")
    @PutMapping(value = "/{organizationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationResponseDTO> updateOrganization(final Authentication authentication,
                                                                      @PathVariable(name = "organizationId") final int organizationId,
                                                                      @RequestPart(value = "image", required = false) final MultipartFile file,
                                                                      @RequestPart("requestDTO") @Valid final OrganizationUpdateRequestDTO requestDTO) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final Organization organization = organizationService.updateOrganization(member, organizationId, file, requestDTO);

        return ResponseEntity.ok(OrganizationResponseDTO.of(organization));
    }

    @Operation(summary = "패널티 초기화")
    @PutMapping("/{organizationId}/members/{organizationMemberId}/penalty")
    public ResponseEntity<OrganizationMemberPenaltyStatusUpdateResponseDTO> updateOrganizationMemberPenalty(final Authentication authentication,
                                                                                                            @PathVariable(name = "organizationId") final int organizationId,
                                                                                                            @PathVariable(name = "organizationMemberId") final int OrganizationMemberId) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        final OrganizationMember organizationMember = organizationService.updateOrganizationMemberPenalty(member, organizationId, OrganizationMemberId);

        return ResponseEntity.ok(OrganizationMemberPenaltyStatusUpdateResponseDTO.of(organizationMember));
    }

    @Operation(summary = "조직 탈퇴")
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<?> leaveOrganization(final Authentication authentication,
                                               @PathVariable(name = "organizationId") final int organizationId) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        organizationService.leaveOrganization(member, organizationId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "조직 멤버 강퇴")
    @DeleteMapping("{organizationId}/members/{organizationMemberId}")
    public ResponseEntity<?> kickOrganizationMember(final Authentication authentication,
                                                    @PathVariable(name = "organizationId") final int organizationId,
                                                    @PathVariable(name = "organizationMemberId") final int OrganizationMemberId) {
        final Member member = memberService.findMemberByEmail(authentication.getPrincipal().toString());

        organizationService.kickOrganizationMember(member, organizationId, OrganizationMemberId);

        return ResponseEntity.noContent().build();
    }
}