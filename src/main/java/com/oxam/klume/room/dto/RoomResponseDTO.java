package com.oxam.klume.room.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {
    private int id;
    private String name;
    private String description;
    private int capacity;
    private String imageUrl;
    private int organizationId;

}
