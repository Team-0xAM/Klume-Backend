package com.oxam.klume.room.service;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.exception.ReservationExistsException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.exception.AvailableTimeNotFoundException;
import com.oxam.klume.room.exception.AvailableTimeOverlapException;
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
    private final DailyReservationRepository dailyReservationRepository;


    @Override
    public List<AvailableTimeResponseDTO> getAvailableTimesByRoom(final int memberId, final int roomId, final int organizationId) {
        findRoomByIdAndOrganization(roomId, organizationId);
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN );

        List<AvailableTime> availableTimes = availableTimeRepository.findAllByRoomId(roomId);
        return availableTimes.stream()
                .map(AvailableTimeResponseDTO::of)
                .toList();
    }

    @Transactional
    @Override
    public AvailableTimeResponseDTO createAvailableTime(
            final int memberId,
            final int organizationId,
            final int roomId,
            final AvailableTimeRequestDTO request
    ) {

        Room room = findRoomByIdAndOrganization(roomId, organizationId);
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN );

        // 해당 회의실의 기존의 예약 가능 시간과 겹치는 시간이 있는지 확인
        List<AvailableTime> otherTimes = availableTimeRepository.findAllByRoomId(roomId);
        validateNoOverlap(request, otherTimes);

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
    public AvailableTimeResponseDTO updateAvailableTime(final int memberId, final int organizationId, final int availableTimeId, final AvailableTimeRequestDTO request) {
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN);

        AvailableTime availableTime = availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);

        // 예약이 존재하지 않는지 확인
        validateNoReservation(availableTimeId);

        // 현재 회의실의 다른 예약 가능 시간 가져오기
        List<AvailableTime> otherTimes = availableTimeRepository.findAllByRoomId(availableTime.getRoom().getId())
                .stream()
                .filter(t -> t.getId() != availableTimeId)
                .toList();

        // 요일 + 시간 + 기간 겹침여부 확인
        validateNoOverlap(request, otherTimes);

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

        // 수정된 예약 가능 시간에 맞춰 DailyAvailableTime 재생성
        createFromAvailableTime(saved);

        return AvailableTimeResponseDTO.of(saved);
    }


    // 예약 가능 시간 삭제
    @Transactional
    @Override
    public void deleteAvailableTime(final int memberId, final int organizationId, final int availableTimeId) {
        Organization organization = findOrganizationById(organizationId);
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN );

        AvailableTime availableTime = availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);

        // 예약이 존재한다면 예외
        validateNoReservation(availableTimeId);

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
    private Room findRoomByIdAndOrganization(int roomId, int organizationId) {
        return roomRepository.findByIdAndOrganizationId(roomId, organizationId)
                .orElseThrow(RoomNotFoundException::new);
    }

    private Organization findOrganizationById(final int organizationId){
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("조직이 존재하지 않습니다"));
    }

    private void validateAdminPermission(final int memberId, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role)) {
            throw new OrganizationNotAdminException();
        }
    }

    // 해당 예약 가능 시간에 예약이 존재하는지 확인
    private void validateNoReservation(int availableTimeId) {
        AvailableTime availableTime = availableTimeRepository.findById(availableTimeId)
                .orElseThrow(AvailableTimeNotFoundException::new);

        boolean hasReservation = dailyReservationRepository.existsByDailyAvailableTime_AvailableTime(availableTime);
        if (hasReservation) {
            throw new ReservationExistsException();
        }
    }

    // 설명: 예약 등록, 수정, 삭제시 기존의 회의실 예약 가능 시간과 겹치는 부분이 있는지 확인
    private void validateNoOverlap(AvailableTimeRequestDTO request, List<AvailableTime> otherTimes) {
        LocalDate reqStart = LocalDate.parse(request.getRepeatStartDay());
        LocalDate reqEnd = LocalDate.parse(request.getRepeatEndDay());

        for (AvailableTime other : otherTimes) {
            LocalDate otherStart = LocalDate.parse(other.getRepeatStartDay());
            LocalDate otherEnd = LocalDate.parse(other.getRepeatEndDay());

            // 기간이 겹치는지 확인
            boolean periodOverlap = !(reqEnd.isBefore(otherStart) || reqStart.isAfter(otherEnd));

            if (!periodOverlap) continue;

            // 요일이 겹치는지 확인
            boolean dayOverlap =
                    (request.isMon() && other.isMon()) ||
                            (request.isTue() && other.isTue()) ||
                            (request.isWed() && other.isWed()) ||
                            (request.isThu() && other.isThu()) ||
                            (request.isFri() && other.isFri()) ||
                            (request.isSat() && other.isSat()) ||
                            (request.isSun() && other.isSun());

            if (!dayOverlap) continue;

            // 시간대가 겹치는지 확인
            boolean timeOverlap = !(request.getAvailableEndTime().compareTo(other.getAvailableStartTime()) <= 0
                    || request.getAvailableStartTime().compareTo(other.getAvailableEndTime()) >= 0);

            if (timeOverlap) {
                throw new AvailableTimeOverlapException();
            }
        }
    }

}
