package com.oxam.klume.room.service;


import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


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

        validateNoReservation(dailyAvailableTimeId);

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
    
    @Transactional(readOnly = true)
    @Override
    public List<DailyAvailableTimeResponseDTO> getTodayOpeningTimes(final int memberId, final int organizationId) {
        Organization organization = findOrganizationById(organizationId);

        // 조직 멤버 권한 검증
        organizationMemberRepository.findByMemberIdAndOrganization(memberId, organization)
                .orElseThrow(OrganizationNotFoundException::new);

        // 오늘 날짜 (YYYY-MM-DD 형식)
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 오늘 이후 이용 가능한 일자별 예약 가능 시간 조회
        List<DailyAvailableTime> dailyAvailableTimes =
                dailyAvailableTimeRepository.findByOrganizationIdAndReservationOpenDay(organizationId, today);

        return dailyAvailableTimes.stream()
                .map(DailyAvailableTimeResponseDTO::of)
                .collect(Collectors.toList());
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
    private void validateNoReservation(int dailyAvailableTimeId) {
        if (dailyReservationRepository.findByDailyAvailableTime_Id(dailyAvailableTimeId).isPresent()) {
            throw new ReservationExistsException();
        }
    }
}