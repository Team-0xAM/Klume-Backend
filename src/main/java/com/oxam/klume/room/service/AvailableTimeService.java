package com.oxam.klume.room.service;

import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AvailableTimeService {
    AvailableTimeResponseDTO createAvailableTime(final int memberId, final int organizationId, final int roomId, final AvailableTimeRequestDTO request);

    AvailableTimeResponseDTO updateAvailableTime(final int memberId, final int organizationId, final int availableTimeId, final AvailableTimeRequestDTO request);

    void deleteAvailableTime(final int memberId, final int organizatonId, final int availableTimeId);

    List<AvailableTimeResponseDTO> getAvailableTimesByRoom(final int memberId, final int roomId, final int organizationId);
}
