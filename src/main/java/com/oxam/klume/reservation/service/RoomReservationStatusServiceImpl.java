package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dao.ReservationMapper;
import com.oxam.klume.reservation.dto.ReservationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomReservationStatusServiceImpl implements RoomReservationStatusService {
    private final ReservationMapper reservationMapper;

    @Override
    public List<ReservationResponseDTO> findRoomStatusByDay(final int organizationId, final int roomId, final String date) {
        return reservationMapper.selectRoomStatusByDay(organizationId, roomId, date);
    }

    @Override
    public List<ReservationResponseDTO> findRoomStatusByWeek(final int organizationId, final int roomId, final String startDate, final String endDate) {
        return reservationMapper.selectRoomStatusByWeek(organizationId, roomId, startDate, endDate);

    }
}
