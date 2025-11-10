package com.oxam.klume.organization.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oxam.klume.organization.entity.OrganizationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationGroupResponseDTO {
    @Schema(description = "조직 그룹 id", example = "1")
    private int organizationGroupId;

    @Schema(description = "조직 그룹명", example = "19기")
    private String name;

    @Schema(description = "조직 그룹 설정", example = "BEYOND SW 캠프 19기 교육 과정 그룹입니다.")
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "조직 내 회원의 수", example = "1")
    private Integer memberCount;

    public static OrganizationGroupResponseDTO of(final OrganizationGroup organizationGroup, final Integer memberCount) {
        return new OrganizationGroupResponseDTO(organizationGroup.getId(),
                organizationGroup.getName(),
                organizationGroup.getDescription()
                , memberCount);
    }
}