package com.oxam.klume.reservation.service;

import com.oxam.klume.reservation.dto.MyReservationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyReservationService {
    List<MyReservationDTO> selectMyReservations(final int organizationId, final int memberId);
    void cancelReservation(final int reservationId, final int organizationId, final int memberId);
    void enterRoom(final int memberId, final int reservationId, final int organizationId, final MultipartFile file);

    String getReservationPhoto(final int memberId, final int reservationId, final int organizationId);
}
