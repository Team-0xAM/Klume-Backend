package com.oxam.klume.reservation.controller;

import com.oxam.klume.reservation.dto.ReservationResponseDTO;
import com.oxam.klume.reservation.service.RoomReservationStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회의실별 예약 현황 조회 API")
@RestController
@RequestMapping("/organizations/{organizationId}/rooms/{roomId}/reservations/status")
@RequiredArgsConstructor
public class RoomReservationStatusController {

    private final RoomReservationStatusService roomReservationStatusService;

    @GetMapping("/day")
    public List<ReservationResponseDTO> getRoomStatusByDay(
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @RequestParam final String date
    ) {
        return roomReservationStatusService.findRoomStatusByDay(organizationId, roomId, date);
    }

    @GetMapping("/week")
    public List<ReservationResponseDTO> getRoomStatusByWeek(
            @PathVariable final int organizationId,
            @PathVariable final int roomId,
            @RequestParam final String startDate,
            @RequestParam final String endDate
    ) {
        return roomReservationStatusService.findRoomStatusByWeek(organizationId, roomId, startDate, endDate);
    }
}



