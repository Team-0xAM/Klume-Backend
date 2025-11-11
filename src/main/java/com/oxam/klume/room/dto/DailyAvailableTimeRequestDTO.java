package com.oxam.klume.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyAvailableTimeRequestDTO {
    @NotNull(message = "AvailableTimeId는 필수입니다.")
    @Schema(description = "이용 가능 시간 id", example = "1")
    private int availableTimeId;

    @NotBlank(message = "날짜는 필수입니다.")
    @Schema(description = "날짜", example = "2025-11-10")
    private String date;

    @NotBlank(message = "이용 가능 시작 시간은 필수입니다.")
    @Schema(description = "이용 가능 시작 시간", example = "09:00")
    private String availableStartTime;

    @NotBlank(message = "이용 가능 마감 시간은 필수입니다.")
    @Schema(description = "이용 가능 마감 시간", example = "10:00")
    private String availableEndTime;

    @Schema(description = "예약 오픈일", example = "2025-11-09")
    private String reservationOpenDay;

    @Schema(description = "예약 오픈 시간", example = "08:50")
    private String reservationOpenTime;


}
