package com.oxam.klume.room.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequestDTO {
    private String name;
    private String description;
    private int capacity;
    private String imageUrl;    // s3 업로드
}
