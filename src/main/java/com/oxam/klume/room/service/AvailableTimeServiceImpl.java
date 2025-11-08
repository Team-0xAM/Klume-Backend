package com.oxam.klume.room.service;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.exception.AvailableTimeNotFoundException;
import com.oxam.klume.room.exception.RoomNotFoundException;
import com.oxam.klume.room.repository.AvailableTimeRepository;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import com.oxam.klume.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableTimeServiceImpl implements AvailableTimeService{
    private final OrganizationRepository organizationRepository;
    private final RoomRepository roomRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final DailyAvailableTimeRepository dailyAvailableTimeRepository;
    private final OrganizationMemberRepository organizationMemberRepository;



    @Transactional
    @Override
    public AvailableTimeResponseDTO createAvailableTime(
            final int memberId,
            final int organizationId,
            final int roomId,
            final AvailableTimeRequestDTO request
    ) {

        Room room = findRoomById(roomId);
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN );

        // TODO 같은 회의실 예약 가능 시간이 겹치는 경우 예외 반환

        AvailableTime availableTime = AvailableTime.create(
                request.isMon(),
                request.isTue(),
                request.isWed(),
                request.isThu(),
                request.isFri(),
                request.isSat(),
                request.isSun(),
                request.getName(),
                request.getAvailableStartTime(),
                request.getAvailableEndTime(),
                request.getReservationOpenDay(),
                request.getReservationOpenTime(),
                request.getRepeatStartDay(),
                request.getRepeatEndDay(),
                request.getTimeInterval(),
                room
        );

        AvailableTime saved = availableTimeRepository.save(availableTime);

        // Daily_Available_Time 테이블에 데이터 삽입
        createFromAvailableTime(saved);

        return AvailableTimeResponseDTO.of(saved);
    }

    // 예약 가능 시간 수정
    @Transactional
    @Override
    public AvailableTimeResponseDTO updateAvailableTime(final int availableTimeId, final AvailableTimeRequestDTO request) {
        AvailableTime availableTime = availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);

        // TODO 같은 회의실 예약 가능 시간이 겹치는 경우 예외 반환

        // TODO 예약이 존재할 경우 예외 반환

        // 기존 DailyAvailableTime 모두 삭제
        dailyAvailableTimeRepository.deleteAllByAvailableTime(availableTime);

        availableTime.update(
                request.isMon(),
                request.isTue(),
                request.isWed(),
                request.isThu(),
                request.isFri(),
                request.isSat(),
                request.isSun(),
                request.getName(),
                request.getAvailableStartTime(),
                request.getAvailableEndTime(),
                request.getReservationOpenDay(),
                request.getReservationOpenTime(),
                request.getRepeatStartDay(),
                request.getRepeatEndDay(),
                request.getTimeInterval()
        );

        AvailableTime saved = availableTimeRepository.save(availableTime);

        // 수정된 예약 가능 시간에 맞춰 DailyAvailableTime 생성
        createFromAvailableTime(saved);

        return AvailableTimeResponseDTO.of(saved);
    }

    // 예약 가능 시간 삭제
    @Transactional
    @Override
    public void deleteAvailableTime(final int availableTimeId) {
        AvailableTime availableTime = availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);

        // TODO: 예약이 존재할 경우 예외 반환

        dailyAvailableTimeRepository.deleteAllByAvailableTime(availableTime);

        availableTimeRepository.delete(availableTime);
    }


    public void createFromAvailableTime(AvailableTime availableTime) {
        // 반복 시작일, 종료일
        LocalDate startDate = LocalDate.parse(availableTime.getRepeatStartDay());
        LocalDate endDate = LocalDate.parse(availableTime.getRepeatEndDay());

        // 사용 가능한 시간 구간
        LocalTime startTime = LocalTime.parse(availableTime.getAvailableStartTime());
        LocalTime endTime = LocalTime.parse(availableTime.getAvailableEndTime());

        // 시간 간격
        Integer intervalMinutes = availableTime.getTimeInterval();

        // 예약 오픈 전일수
        Integer reservationOpenDayCount = availableTime.getReservationOpenDay();

        ArrayList<DailyAvailableTime> dailyList = new ArrayList<>();

        // 시작일부터 종료일까지 하루씩 순회
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // 해당 날짜의 요일이 AvailableTime에서 허용된 요일인지 체크
            boolean isAvailableDay =
                    (dayOfWeek == DayOfWeek.MONDAY && availableTime.isMon()) ||
                            (dayOfWeek == DayOfWeek.TUESDAY && availableTime.isTue()) ||
                            (dayOfWeek == DayOfWeek.WEDNESDAY && availableTime.isWed()) ||
                            (dayOfWeek == DayOfWeek.THURSDAY && availableTime.isThu()) ||
                            (dayOfWeek == DayOfWeek.FRIDAY && availableTime.isFri()) ||
                            (dayOfWeek == DayOfWeek.SATURDAY && availableTime.isSat()) ||
                            (dayOfWeek == DayOfWeek.SUNDAY && availableTime.isSun());

            if (!isAvailableDay) continue;

            // reservationOpenDay가 null이 아니면 repeatStartDay 기준으로 며칠 전인지 설정
            String reservationOpenDay = null;
            if (reservationOpenDayCount != null) {
                reservationOpenDay = date.minusDays(reservationOpenDayCount).toString();
            }

            if (intervalMinutes == null) {
                // interval이 없으면 하루에 하나만 생성
                DailyAvailableTime daily = DailyAvailableTime.builder()
                        .availableTime(availableTime)
                        .date(date.toString())
                        .availableStartTime(startTime.toString())
                        .availableEndTime(endTime.toString())
                        .reservationOpenDay(reservationOpenDay)
                        .reservationOpenTime(availableTime.getReservationOpenTime())
                        .build();
                dailyList.add(daily);
            } else {
                // 하루 중 가능한 시간 구간을 intervalMinutes 간격으로 생성
                for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(intervalMinutes)) {
                    LocalTime nextTime = time.plusMinutes(intervalMinutes);
                    if (nextTime.isAfter(endTime)) break;

                    DailyAvailableTime daily = DailyAvailableTime.builder()
                            .availableTime(availableTime)
                            .date(date.toString())
                            .availableStartTime(time.toString())
                            .availableEndTime(nextTime.toString())
                            .reservationOpenDay(reservationOpenDay)
                            .reservationOpenTime(availableTime.getReservationOpenTime())
                            .build();
                    dailyList.add(daily);
                }
            }
        }

        // 생성된 모든 일별 가능한 시간 구간을 한 번에 저장
        dailyAvailableTimeRepository.saveAll(dailyList);
    }


    // ================= 공통 메서드 =====================
    private Room findRoomById(int roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(RoomNotFoundException::new);
    }

    private Organization findOrganizationById(int organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    private void validateAdminPermission(final int memberId, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role)) {
            throw new OrganizationNotAdminException();
        }
    }
}
