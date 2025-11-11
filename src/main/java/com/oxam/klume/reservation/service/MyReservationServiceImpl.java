package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dao.MyReservationMapper;
import com.oxam.klume.reservation.dto.MyReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReservationServiceImpl implements MyReservationService {
    private final MyReservationMapper myReservationMapper;

    public List<MyReservationDTO> selectMyReservations(int organizationMemberId) {
        return myReservationMapper.selectMyReservations(organizationMemberId);
    }
}
