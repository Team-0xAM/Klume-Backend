package com.oxam.klume.organization.controller;

import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "organization", description = "조직 관련 API")
@RequestMapping("/organizations")
@RequiredArgsConstructor
@RestController
public class OrganizationController {
    private final OrganizationService organizationService;

    @Operation(summary = "조직 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createOrganization(@RequestPart(value = "image", required = false) final MultipartFile file,
                                                @RequestPart("requestDTO") @Valid final OrganizationRequestDTO requestDTO) {
        // TODO 로그인한 회원 ID 가져오기
        final int memberId = 7;

        organizationService.createOrganization(memberId, file, requestDTO);

        return ResponseEntity.ok().build();
    }
}