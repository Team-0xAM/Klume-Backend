package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationNoticeRequest {
    @Schema(description = "제목", example = "5층 회의실 사용불가")
    private String title;

    @Schema(description = "내용", example = "사유: 특강")
    private String content;
}
