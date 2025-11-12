package com.oxam.klume.room.controller;


import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.room.dto.DailyAvailableTimeRequestDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeResponseDTO;
import com.oxam.klume.room.service.DailyAvailableTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "일자별 예약 가능 시간 API", description = "일자별 예약 가능 시간 관련 API")
@RestController
@RequestMapping("/organizations/{organizationId}/daily-available-times")
@RequiredArgsConstructor
public class DailyAvailableTimeController {
    private final DailyAvailableTimeService dailyAvailableTimeService;
    private final MemberService memberService;

    @Operation(summary = "오늘 오픈되는 예약 가능 시간 조회", description = "오늘 예약이 오픈되는 일자별 예약 가능 시간 목록을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<List<DailyAvailableTimeResponseDTO>> getTodayOpeningTimes(
            final Authentication authentication,
            @PathVariable final int organizationId
    ) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();
        List<DailyAvailableTimeResponseDTO> response = dailyAvailableTimeService.getTodayOpeningTimes(memberId, organizationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일자별 이용 가능 시간 수정")
    @PutMapping("/{dailyAvailableTimeId}")
    public ResponseEntity<DailyAvailableTimeResponseDTO> updateDailyAvailableTime(
            final Authentication authentication,
            @PathVariable final int organizationId,
            @PathVariable final int dailyAvailableTimeId,
            @Valid @RequestBody final DailyAvailableTimeRequestDTO request
    ) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        DailyAvailableTimeResponseDTO response =
                dailyAvailableTimeService.updateDailyAvailableTime(memberId, organizationId, dailyAvailableTimeId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일자별 이용 가능 시간 삭제")
    @DeleteMapping("/{dailyAvailableTimeId}")
    public ResponseEntity<String> deleteDailyAvailableTime(
            final Authentication authentication,
            @PathVariable final int organizationId,
            @PathVariable final int dailyAvailableTimeId
    ) {
        final int memberId = memberService.findMemberByEmail(authentication.getPrincipal().toString()).getId();

        dailyAvailableTimeService.deleteDailyAvailableTime(memberId, organizationId, dailyAvailableTimeId);
        return ResponseEntity.ok("해당 일자별 이용 가능 시간이 삭제되었습니다.");
    }



}
