package com.oxam.klume.organization.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oxam.klume.organization.entity.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationResponseDTO {
    @Schema(description = "조직 id", example = "1")
    private int organizationId;

    @Schema(description = "조직명", example = "한화 시스템 BEYOND SW 캠프")
    private String name;

    @Schema(description = "조직 설명", example = "개발자 부트캠프로, 미래 소프트웨어 인재 양성을 목표로 합니다.")
    private String description;

    @Schema(description = "조직 이미지 url", example = "https://picsum.photos/300")
    private String imageUrl;

    public static OrganizationResponseDTO of(final Organization organization) {
        return new OrganizationResponseDTO(organization.getId(), organization.getName(), organization.getDescription(),
                organization.getImageUrl());
    }
}