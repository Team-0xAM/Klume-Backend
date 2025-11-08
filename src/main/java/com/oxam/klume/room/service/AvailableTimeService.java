package com.oxam.klume.room.service;

import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import jakarta.validation.Valid;

public interface AvailableTimeService {
    AvailableTimeResponseDTO createAvailableTime(final int memberId, final int organizationId, final int roomId, final AvailableTimeRequestDTO request);

    AvailableTimeResponseDTO updateAvailableTime(final int availableTimeId, final AvailableTimeRequestDTO request);
}
