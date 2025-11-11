package com.oxam.klume.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvailableTimeRequestDTO {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    private boolean isMon;
    private boolean isTue;
    private boolean isWed;
    private boolean isThu;
    private boolean isFri;
    private boolean isSat;
    private boolean isSun;

    @NotBlank(message = "시작 시간은 필수입니다.")
    @Schema(description = "시작시간", example = "09:00")
    private String availableStartTime;

    @NotBlank(message = "종료 시간은 필수입니다. ")
    @Schema(description = "종료시간", example = "11:00")
    private String availableEndTime;

    @Schema(description = "예약 오픈 날", example = "1")
    private Integer reservationOpenDay;
    @Schema(description = "예약 오픈 시간", example = "09:00")
    private String reservationOpenTime;

    @NotBlank(message = "반복 시작일은 필수입니다.")
    @Schema(description = "반복 시작일", example = "2025-11-11")
    private String repeatStartDay;

    @NotBlank(message = "반복 종료일은 필수입니다.")
    @Schema(description = "반복 종료일", example = "2025-11-12")
    private String repeatEndDay;

    @Schema(description = "반복 시간 간격", example = "60")
    private Integer timeInterval;
}
