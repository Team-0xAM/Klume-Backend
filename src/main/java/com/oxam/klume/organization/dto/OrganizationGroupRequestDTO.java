package com.oxam.klume.organization.dto;

import com.oxam.klume.organization.entity.OrganizationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationGroupRequestDTO {
    @NotBlank(message = "그룹명을 입력해주세요.")
    @Schema(description = "그룹명", defaultValue = "19기")
    private String name;

    @NotBlank(message = "그룹 설명을 입력해주세요.")
    @Schema(description = "그룹 설명", defaultValue = "BEYOND SW캠프 19기 교육 과정 그룹입니다.")
    private String description;

    public static OrganizationGroup toEntity(final OrganizationGroupRequestDTO requestDTO) {
        return new OrganizationGroup(requestDTO.getName(), requestDTO.getDescription());
    }
}