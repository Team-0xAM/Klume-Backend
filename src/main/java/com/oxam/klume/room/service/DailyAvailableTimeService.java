package com.oxam.klume.room.service;

import com.oxam.klume.room.dto.DailyAvailableTimeRequestDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import jakarta.validation.Valid;

public interface DailyAvailableTimeService {

    DailyAvailableTimeResponseDTO updateDailyAvailableTime(final int memberId, final int organizationId, final int dailyAvailableTimeId, @Valid final DailyAvailableTimeRequestDTO request);
}
