package com.oxam.klume.room.service;

import com.oxam.klume.room.dto.DailyAvailableTimeRequestDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import jakarta.validation.Valid;

import java.util.List;

public interface DailyAvailableTimeService {

    DailyAvailableTimeResponseDTO updateDailyAvailableTime(final int memberId, final int organizationId, final int dailyAvailableTimeId, @Valid final DailyAvailableTimeRequestDTO request);

    void deleteDailyAvailableTime(final int memberId, final int organizationId, final int dailyAvailableTimeId);

    List<DailyAvailableTimeResponseDTO> getTodayOpeningTimes(final int memberId, final int organizationId);
}
