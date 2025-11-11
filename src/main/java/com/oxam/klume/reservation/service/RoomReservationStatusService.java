package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dto.ReservationResponseDTO;

import java.util.List;

public interface RoomReservationStatusService {
    List<ReservationResponseDTO> findRoomStatusByDay(final int organizationId, final int roomId, final String date);

    List<ReservationResponseDTO> findRoomStatusByWeek(final int organizationId, final int roomId, final String startDate, final String endDate);
}
