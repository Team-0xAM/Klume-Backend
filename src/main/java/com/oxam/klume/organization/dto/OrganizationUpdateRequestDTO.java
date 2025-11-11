package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUpdateRequestDTO {
    @NotBlank(message = "조직명을 입력해주세요.")
    @Schema(description = "조직명", defaultValue = "한화 시스템 BEYOND SW 캠프")
    private String name;

    @NotBlank(message = "조직에 관한 설명을 입력해주세요.")
    @Schema(description = "조직 설명", defaultValue = "개발자 부트캠프로, 미래 소프트웨어 인재 양성을 목표로 합니다.")
    private String description;

    @Schema(description = "조직 S3 이미지 url", defaultValue = "https://bucket.3.region.amazonaws.com/12345.png")
    private String imageUrl;
}