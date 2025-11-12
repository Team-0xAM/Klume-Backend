package com.oxam.klume.reservation.controller;

import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.reservation.dto.MyReservationDTO;
import com.oxam.klume.reservation.service.MyReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "내 예약 관련 API")
@RestController
@RequestMapping("/organizations/{organizationId}/reservations")
@RequiredArgsConstructor
public class MyReservationController {
    private final MemberService memberService;
    private final MyReservationService myReservationService;

    @Operation(summary = "내 예약 목록 조회")
    @GetMapping("/my")
    public ResponseEntity<List<MyReservationDTO>> getMyReservations(
            @PathVariable int organizationId,
            final Authentication authentication)
    {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        return ResponseEntity.ok(myReservationService.selectMyReservations(organizationId, memberId));
    }

    @Operation(summary = "내 예약 취소")
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable final int reservationId,
            @PathVariable final int organizationId,
            Authentication authentication) {

        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        myReservationService.cancelReservation(reservationId, organizationId, memberId);
        return ResponseEntity.ok().build();
    }

}
