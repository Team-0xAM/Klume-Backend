package com.oxam.klume.reservation.dao;

import com.oxam.klume.reservation.dto.MyReservationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyReservationMapper {
    List<MyReservationDTO> selectMyReservations(int organizationMemberId);
}
