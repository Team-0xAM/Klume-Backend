package com.oxam.klume.reservation.dao;

import com.oxam.klume.reservation.dto.ReservationResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationMapper {
    List<ReservationResponseDTO> selectRoomStatusByDay(int organizationId, int roomId, String date);

    List<ReservationResponseDTO> selectRoomStatusByWeek(int organizationId, int roomId, String startDate, String endDate);

    List<ReservationResponseDTO> selectOrganizationRoomStatusByDay(int organizationId, String date);

    List<ReservationResponseDTO> selectOrganizationRoomStatusByWeek(int organizationId, String startDate, String endDate);
}
