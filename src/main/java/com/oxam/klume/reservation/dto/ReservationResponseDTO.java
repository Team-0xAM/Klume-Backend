package com.oxam.klume.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    private int reservationId;
    private int dailyAvailableTimeId;
    private int roomId;
    private String roomName;
    private String timeName;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private int reservationOpenDay;
    private String reservationOpenTime;
    private String reservedMember;
}