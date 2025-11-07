package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationNoticeRequest {
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Schema(description = "제목", example = "5층 회의실 사용불가")
    private String title;

    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    @Schema(description = "내용", example = "사유: 특강")
    private String content;
}
