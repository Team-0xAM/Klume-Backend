package com.oxam.klume.room.service;


import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.exception.ReservationExistsException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.room.dto.DailyAvailableTimeRequestDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.exception.AvailableTimeNotFoundException;
import com.oxam.klume.room.exception.DailyAvailableTimeNotFoundException;
import com.oxam.klume.room.repository.AvailableTimeRepository;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DailyAvailableTimeServiceImpl implements DailyAvailableTimeService {
    private final DailyAvailableTimeRepository dailyAvailableTimeRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final DailyReservationRepository dailyReservationRepository;

    @Transactional
    @Override
    public DailyAvailableTimeResponseDTO updateDailyAvailableTime(final int memberId, final int organizationId, final int dailyAvailableTimeId, final DailyAvailableTimeRequestDTO request) {
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN);

        AvailableTime availableTime = findAvailableTimeById(request.getAvailableTimeId());
        DailyAvailableTime dailyAvailableTime = findDailyAvailableTimeById(dailyAvailableTimeId);

        validateNoReservation(request.getAvailableTimeId());

        dailyAvailableTime.update(
                request.getDate(),
                request.getAvailableStartTime(),
                request.getAvailableEndTime(),
                request.getReservationOpenDay(),
                request.getReservationOpenTime(),
                availableTime

        );

        return DailyAvailableTimeResponseDTO.of(dailyAvailableTime);
    }

    @Transactional
    @Override
    public void deleteDailyAvailableTime(final int memberId, final int organizationId, final int dailyAvailableTimeId) {
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN);
        DailyAvailableTime dailyAvailableTime = findDailyAvailableTimeById(dailyAvailableTimeId);

        validateNoReservation(dailyAvailableTimeId);

        dailyAvailableTimeRepository.delete(dailyAvailableTime);
    }


    // ============================== 공통 메서드 =====================================
    private Organization findOrganizationById(final int organizationId){
        return organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    private DailyAvailableTime findDailyAvailableTimeById(final int dailyAvailableTimeId){
        return dailyAvailableTimeRepository.findById(dailyAvailableTimeId)
                .orElseThrow(DailyAvailableTimeNotFoundException::new);
    }

    private AvailableTime findAvailableTimeById(final int availableTimeId){
        return availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);
    }

    private void validateAdminPermission(final int memberId, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role)) {
            throw new OrganizationNotAdminException();
        }
    }

    // 해당 일자의 예약 가능 시간에 예약이 존재하는지 확인
    private DailyReservation validateNoReservation(int dailyAvailableTimeId) {
        return dailyReservationRepository.findById(dailyAvailableTimeId)
                .orElseThrow(ReservationExistsException::new);

    }


}