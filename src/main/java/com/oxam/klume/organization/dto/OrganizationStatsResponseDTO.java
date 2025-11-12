package com.oxam.klume.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationStatsResponseDTO {
    @Schema(description = "조직 id", example = "1")
    private int organizationId;

    @Schema(description = "조직명", example = "한화 시스템 BEYOND SW 캠프")
    private String name;

    @Schema(description = "조직 설명", example = "개발자 부트캠프로, 미래 소프트웨어 인재 양성을 목표로 합니다.")
    private String description;

    @Schema(description = "조직 이미지 url", example = "https://picsum.photos/300")
    private String imageUrl;

    @Schema(description = "조직 구성원 수", example = "42")
    private int memberCount;

    @Schema(description = "조직 회의실 수", example = "7")
    private int roomCount;

    @Schema(description = "총 예약 수", example = "156")
    private int totalReservationCount;

    @Schema(description = "오늘 예약 수", example = "5")
    private int todayReservationCount;
}
