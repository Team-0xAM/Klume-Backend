package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dto.ReservationResponseDTO;

import java.util.List;

public interface OrganizationReservationStatusService {
    List<ReservationResponseDTO> findOrganizationRoomStatusByDay(int organizationId, String date);

    List<ReservationResponseDTO> findOrganizationRoomStatusByWeek(int organizationId, String startDate, String endDate);
}
