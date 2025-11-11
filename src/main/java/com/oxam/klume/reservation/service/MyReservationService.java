package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dto.MyReservationDTO;

import java.util.List;

public interface MyReservationService {
    List<MyReservationDTO> selectMyReservations(int organizationMemberId);
}
