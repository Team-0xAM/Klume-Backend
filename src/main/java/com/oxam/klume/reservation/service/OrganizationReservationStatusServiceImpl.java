package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dao.ReservationMapper;
import com.oxam.klume.reservation.dto.ReservationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationReservationStatusServiceImpl implements OrganizationReservationStatusService {
    private final ReservationMapper reservationMapper;

    @Override
    public List<ReservationResponseDTO> findOrganizationRoomStatusByDay(int organizationId, String date) {
        return reservationMapper.selectOrganizationRoomStatusByDay(organizationId, date);
    }

    @Override
    public List<ReservationResponseDTO> findOrganizationRoomStatusByWeek(int organizationId, String startDate, String endDate) {
        return reservationMapper.selectOrganizationRoomStatusByWeek(organizationId, startDate, endDate);
    }
}
