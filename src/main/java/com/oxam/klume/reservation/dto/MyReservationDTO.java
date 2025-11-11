package com.oxam.klume.reservation.dto;

import lombok.Data;

@Data
public class MyReservationDTO {
    private int reservationId;
    private String roomName;
    private String reservationCreatedAt;
    private String reservationDate;
    private String startTime;
    private String endTime;
    private String cancelledAt;
    private String reservationStatus;
}