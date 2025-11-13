package com.oxam.klume.reservation.controller;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.reservation.dto.RoomReserveResponseDTO;
import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reservation", description = "예약 관련 API")
@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final MemberService memberService;
    private final ReservationService reservationService;

    @Operation(summary = "회의실 예약")
    @PostMapping("/organizations/{organizationId}/rooms/{roomId}/reservations/{dailyAvailableTimeId}")
    public ResponseEntity<RoomReserveResponseDTO> reserveRoom(final Authentication authentication,
                                                              @PathVariable("organizationId") final int organizationId,
                                                              @PathVariable("roomId") final int roomId,
                                                              @PathVariable("dailyAvailableTimeId") final int dailyAvailableTimeId) {
        final Member member = memberService.findMemberByEmail(authentication.getName());

        final DailyReservation dailyReservation =
                reservationService.reserveRoom(member, organizationId, roomId, dailyAvailableTimeId);

        return ResponseEntity.ok(RoomReserveResponseDTO.of(dailyReservation));
    }

    @Operation(summary = "회의실 예약 취소")
    @PutMapping("/organizations/{organizationId}/rooms/{roomId}/reservations/{reservationId}")
    public ResponseEntity<RoomReserveResponseDTO> cancelReservation(final Authentication authentication,
                                                              @PathVariable("organizationId") final int organizationId,
                                                              @PathVariable("roomId") final int roomId,
                                                              @PathVariable("reservationId") final int reservationId) {
        final Member member = memberService.findMemberByEmail(authentication.getName());

        final DailyReservation dailyReservation =
                reservationService.cancelReservation(reservationId, organizationId, roomId, member.getId());

        return ResponseEntity.ok(RoomReserveResponseDTO.of(dailyReservation));
    }
}