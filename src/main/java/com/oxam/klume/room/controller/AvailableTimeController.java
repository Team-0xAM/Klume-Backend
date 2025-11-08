package com.oxam.klume.room.controller;

import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import com.oxam.klume.room.service.AvailableTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Available_Time", description = "예약 가능 시간 설정 관련 API")
@RequestMapping("/organizations/{organizationId}/rooms/{roomId}/available-times")
@RequiredArgsConstructor
@RestController
public class AvailableTimeController {
    private final AvailableTimeService availableTimeService;

    // TODO 로그인한 사용자 id 가져오기
    private int memberId = 5;

    @Operation(summary = "예약 가능 시간 등록")
    @PostMapping
    public ResponseEntity<AvailableTimeResponseDTO> createAvailableTime(
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @Valid@RequestBody final AvailableTimeRequestDTO request
    ) {
        AvailableTimeResponseDTO response = availableTimeService.createAvailableTime(memberId, organizationId, roomId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 가능 시간 수정")
    @PutMapping("/{availableTimeId}")
    public ResponseEntity<AvailableTimeResponseDTO> updateAvailableTime(
            @PathVariable("availableTimeId") final int availableTimeId,
            @Valid @RequestBody final AvailableTimeRequestDTO request
    ) {
        AvailableTimeResponseDTO updated = availableTimeService.updateAvailableTime(availableTimeId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "예약 가능 시간 삭제")
    @DeleteMapping("/{availableTimeId}")
    public ResponseEntity<String> deleteAvailableTime(@PathVariable("availableTimeId") final int availableTimeId) {
        availableTimeService.deleteAvailableTime(availableTimeId);

        return ResponseEntity.ok("예약 가능 시간이 삭제되었습니다.");
    }

}
