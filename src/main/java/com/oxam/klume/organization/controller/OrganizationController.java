package com.oxam.klume.organization.controller;

import com.oxam.klume.organization.dto.*;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "organization", description = "조직 관련 API")
@RequestMapping("/organizations")
@RequiredArgsConstructor
@RestController
public class OrganizationController {
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
    public ResponseEntity<OrganizationResponseDTO> createOrganization(@Parameter(name = "memberId") final int memberId,
                                                                      @RequestPart(value = "image", required = false) final MultipartFile file,
                                                                      @RequestPart("requestDTO") @Valid final OrganizationRequestDTO requestDTO) {
        // TODO 로그인한 회원 ID 가져오기

        final Organization organization = organizationService.createOrganization(memberId, file, requestDTO);

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
    public ResponseEntity<OrganizationInvitationCodeResponseDTO> createInvitationCode(@Parameter(name = "memberId") final int memberId,
                                                                                      @PathVariable("organizationId") final int organizationId) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

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
    public ResponseEntity<OrganizationMemberRoleResponseDTO> findOrganizationMemberRole(
            @Parameter(name = "memberId") final int memberId,
            @PathVariable("organizationId") final int organizationId) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

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
    public ResponseEntity<List<OrganizationGroupResponseDTO>> findOrganizationGroups(@Parameter(name = "memberId") final int memberId,
                                                                                     @PathVariable("organizationId") final int organizationId) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

        final List<OrganizationGroup> organizationGroup = organizationService.findOrganizationGroups(memberId, organizationId);

        final List<OrganizationGroupResponseDTO> response = organizationGroup.stream()
                .map(OrganizationGroupResponseDTO::of)
                .collect(Collectors.toList());

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
    public ResponseEntity<OrganizationResponseDTO> validateInvitationCode(@Parameter(name = "memberId") final int memberId,
                                                                          @RequestBody @Valid final OrganizationInvitationCodeRequestDTO requestDTO) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜

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
    public ResponseEntity<OrganizationMemberResponseDTO> createOrganizationMember(@Parameter(name = "memberId") final int memberId,
                                                                                  @PathVariable("organizationId") final int organizationId,
                                                                                  @RequestBody @Valid final OrganizationMemberRequestDTO requestDTO) {
        // TODO 로그인한 회원 ID 가져오기  by 지륜
        final OrganizationMember organizationMember = organizationService.createOrganizationMember(memberId, organizationId, requestDTO);

        final OrganizationMemberResponseDTO response = OrganizationMemberResponseDTO.of(organizationMember);

        return ResponseEntity.ok(response);
    }
}