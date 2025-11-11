package com.oxam.klume.reservation.controller;

import com.oxam.klume.reservation.dto.ReservationResponseDTO;
import com.oxam.klume.reservation.service.OrganizationReservationStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "조직별 회의실 예약 현황 조회 API")
@RestController
@RequestMapping("/organizations/{organizationId}/reservations/status")
@RequiredArgsConstructor
public class OrganizationReservationStatusController {
    private final OrganizationReservationStatusService organizationReservationStatusService;

    @GetMapping("/day")
    public List<ReservationResponseDTO> getOrganizationRoomStatusByDay(
            @PathVariable final int organizationId,
            @RequestParam final String date
    ) {
        return organizationReservationStatusService.findOrganizationRoomStatusByDay(organizationId, date);
    }

    @GetMapping("/week")
    public List<ReservationResponseDTO> getOrganizationRoomStatusByWeek(
            @PathVariable final int organizationId,
            @RequestParam final String startDate,
            @RequestParam final String endDate
    ) {
        return organizationReservationStatusService.findOrganizationRoomStatusByWeek(organizationId, startDate, endDate);
    }
}
