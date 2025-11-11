package com.oxam.klume.room.controller;

import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import com.oxam.klume.room.service.AvailableTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Available_Time", description = "예약 가능 시간 설정 관련 API")
@RequestMapping("/organizations/{organizationId}/rooms/{roomId}/available-times")
@RequiredArgsConstructor
@RestController
public class AvailableTimeController {
    private final AvailableTimeService availableTimeService;
    private final MemberService memberService;;

    @Operation(summary = "예약 가능 시간 조회")
    @GetMapping
    public ResponseEntity<List<AvailableTimeResponseDTO>> getAvailableTimesByRoom(
            final Authentication authentication,
            @PathVariable final int roomId,
            @PathVariable final int organizationId) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        List<AvailableTimeResponseDTO> list = availableTimeService.getAvailableTimesByRoom(memberId, roomId, organizationId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "예약 가능 시간 등록")
    @PostMapping
    public ResponseEntity<AvailableTimeResponseDTO> createAvailableTime(
            final Authentication authentication,
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @Valid@RequestBody final AvailableTimeRequestDTO request
    ) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        AvailableTimeResponseDTO response = availableTimeService.createAvailableTime(memberId, organizationId, roomId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 가능 시간 수정")
    @PutMapping("/{availableTimeId}")
    public ResponseEntity<AvailableTimeResponseDTO> updateAvailableTime(
            final Authentication authentication,
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @PathVariable final int availableTimeId,
            @Valid @RequestBody final AvailableTimeRequestDTO request
    ) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        AvailableTimeResponseDTO updated = availableTimeService.updateAvailableTime(memberId, organizationId, availableTimeId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "예약 가능 시간 삭제")
    @DeleteMapping("/{availableTimeId}")
    public ResponseEntity<String> deleteAvailableTime(
            final Authentication authentication,
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @PathVariable final int availableTimeId
    ) {

        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        availableTimeService.deleteAvailableTime(memberId, organizationId, availableTimeId);

        return ResponseEntity.ok("예약 가능 시간이 삭제되었습니다.");
    }

}
