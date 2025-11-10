package com.oxam.klume.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FaqRequest {
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Schema(description = "질문 제목", example = "패널티는 어떨 때 받나요?")
    private String title;

    @NotBlank(message = "질문 내용은 비워둘 수 없습니다.")
    @Schema(description = "질문 내용", example = "예약 취소를 했는데, 패널티를 받았습니다. 패널티를 받으면 어떤 일이 생기나요?")
    private String content;

    @NotBlank(message = "답변은 비워둘 수 없습니다.")
    @Schema(description = "답변", example = "예약 취소 패널티가 3회 누적될 시 일주일간 회의실 예약이 제한됩니다.")
    private String answer;
}

