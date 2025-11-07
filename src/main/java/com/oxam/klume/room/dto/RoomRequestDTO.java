package com.oxam.klume.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RoomRequestDTO", description = "회의실 요청 DTO")
public class RoomRequestDTO {

    @Schema(description = "회의실 이름", example = "5층 테이블 4", required = true)
    private String name;

    @Schema(description = "설명", example = "10인용 회의실, 프로젝터 있음")
    private String description;

    @Schema(description = "수용 인원", example = "10")
    private int capacity;
//    private String imageUrl;    // s3 업로드
}
