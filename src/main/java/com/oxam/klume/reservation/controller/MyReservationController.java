package com.oxam.klume.reservation.controller;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.reservation.dto.MyReservationDTO;
import com.oxam.klume.reservation.service.MyReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> cancelReservation(
            @PathVariable final int reservationId,
            @PathVariable final int organizationId,
            Authentication authentication) {

        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        myReservationService.cancelReservation(reservationId, organizationId, memberId);
        return ResponseEntity.ok("예약 취소가 완료되었습니다.");
    }

    @Operation(summary = "회의실 입장 인증사진 업로드")
    @PostMapping("/{reservationId}/photo")
    public ResponseEntity<String> enterRoom(
            @PathVariable final int reservationId,
            @PathVariable final int organizationId,
            @RequestPart(value = "image") final MultipartFile file,
            Authentication authentication
    ){
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();
        myReservationService.enterRoom(memberId, reservationId, organizationId, file);

        return ResponseEntity.ok("인증사진 업로드가 완료되었습니다.");
    }

    @Operation(summary = "회의실 입장 인증사진 조회")
    @GetMapping("/{reservationId}/photo")
    public ResponseEntity<String> getReservationPhoto(
            @PathVariable final int reservationId,
            @PathVariable final int organizationId,
            Authentication authentication
    ){
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();
        String imageUrl = myReservationService.getReservationPhoto(memberId, reservationId, organizationId);

        return ResponseEntity.ok(imageUrl);
    }

}
