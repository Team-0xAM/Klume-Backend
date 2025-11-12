package com.oxam.klume.reservation.service;

import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.dao.MyReservationMapper;
import com.oxam.klume.reservation.dto.MyReservationDTO;
import com.oxam.klume.reservation.entity.DailyReservation;
import com.oxam.klume.reservation.entity.Reservation;
import com.oxam.klume.reservation.exception.ReservationNotFoundException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.reservation.repository.ReservationRepository;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.exception.DailyAvailableTimeNotFoundException;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReservationServiceImpl implements MyReservationService {
    private final MyReservationMapper myReservationMapper;
    private final ReservationRepository reservationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final DailyAvailableTimeRepository dailyAvailableTimeRepository;
    private final DailyReservationRepository dailyReservationRepository;

    public List<MyReservationDTO> selectMyReservations(final int organizationId, final int memberId) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);

        return myReservationMapper.selectMyReservations(organizationMember.getId());
    }

    @Transactional
    @Override
    public void cancelReservation(final int reservationId, final int organizationId, final int memberId) {
        OrganizationMember organizationMember = findOrganizationMemberById(organizationId, memberId);
        Reservation reservation = reservationRepository.findByIdAndOrganizationMember_Id(reservationId, organizationMember.getId())
                .orElseThrow(() -> new ReservationNotFoundException("예약이 존재하지 않습니다"));

        DailyReservation dailyReservation = dailyReservationRepository.findByReservation(reservation);
        dailyReservation.cancel();

        int dailyAvailableTimeId = dailyReservation.getDailyAvailableTime().getId();
        DailyAvailableTime dailyAvailableTime = dailyAvailableTimeRepository.findById(dailyAvailableTimeId)
                .orElseThrow(() -> new DailyAvailableTimeNotFoundException("해당 예약 시간 정보를 찾을 수 없습니다."));

        dailyAvailableTime.reopen(); // 예약 재오픈

        // 사용자가 1시간 이내 취소했을 시에 패널티 부여
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime cancelledAt = LocalDateTime.parse(dailyReservation.getCancelledAt(), formatter);
        String startStr = dailyAvailableTime.getAvailableTime().getRepeatStartDay() + " " +
                dailyAvailableTime.getAvailableStartTime();
        LocalDateTime reservationStart = LocalDateTime.parse(startStr, formatter);

        if (!cancelledAt.isBefore(reservationStart.minusHours(1))) {
            // 1시간 이내 취소면 패널티 적용
            organizationMember.applyPenalty();
        }
    }

    private OrganizationMember findOrganizationMemberById(final int organizationId, final int memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new OrganizationNotFoundException("사용자가 가입하지 않은 조직입니다."));
    }
}
