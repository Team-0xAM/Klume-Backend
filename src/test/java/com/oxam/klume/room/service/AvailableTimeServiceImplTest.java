package com.oxam.klume.room.service;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.reservation.exception.ReservationExistsException;
import com.oxam.klume.reservation.repository.DailyReservationRepository;
import com.oxam.klume.room.dto.AvailableTimeRequestDTO;
import com.oxam.klume.room.dto.AvailableTimeResponseDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeRequestDTO;
import com.oxam.klume.room.dto.DailyAvailableTimeResponseDTO;
import com.oxam.klume.room.entity.AvailableTime;
import com.oxam.klume.room.entity.DailyAvailableTime;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.repository.AvailableTimeRepository;
import com.oxam.klume.room.repository.DailyAvailableTimeRepository;
import com.oxam.klume.room.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailableTimeServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private AvailableTimeRepository availableTimeRepository;
    @Mock
    private DailyAvailableTimeRepository dailyAvailableTimeRepository;
    @Mock
    private OrganizationMemberRepository organizationMemberRepository;
    @Mock
    private DailyReservationRepository dailyReservationRepository;

    @InjectMocks
    private AvailableTimeServiceImpl availableTimeService;

    @Test
    void 예약가능시간_등록성공() {
        // given
        int memberId = 1;
        int orgId = 100;
        int roomId = 50;

        Organization org = new Organization();
        Room room = Room.builder()
                .id(roomId)
                .name("회의실 A")
                .organization(org)
                .capacity(10)
                .build();

        // DTO
        AvailableTimeRequestDTO dto = new AvailableTimeRequestDTO();
        dto.setName("오전타임");
        dto.setMon(true);
        dto.setAvailableStartTime("09:00");
        dto.setAvailableEndTime("12:00");
        dto.setRepeatStartDay("2025-11-01");
        dto.setRepeatEndDay("2025-11-03");
        dto.setReservationOpenDay(1);
        dto.setReservationOpenTime("09:00");

        // Mocking
        when(roomRepository.findByIdAndOrganizationId(roomId, orgId))
                .thenReturn(Optional.of(room));

        when(organizationRepository.findById(orgId))
                .thenReturn(Optional.of(org));

        when(organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, org, OrganizationRole.ADMIN))
                .thenReturn(true);

        when(availableTimeRepository.findAllByRoomId(roomId))
                .thenReturn(List.of()); // 기존 시간 없음 → 겹침X

        AvailableTime savedTime = AvailableTime.create(
                true, false, false, false, false, false, false,
                dto.getName(),
                dto.getAvailableStartTime(),
                dto.getAvailableEndTime(),
                dto.getReservationOpenDay(),
                dto.getReservationOpenTime(),
                dto.getRepeatStartDay(),
                dto.getRepeatEndDay(),
                dto.getTimeInterval(),
                room
        );

        when(availableTimeRepository.save(any())).thenReturn(savedTime);

        // when
        AvailableTimeResponseDTO response = availableTimeService.createAvailableTime(
                memberId, orgId, roomId, dto
        );

        // then
        assertNotNull(response);
        assertEquals(dto.getName(), response.getName());
        assertEquals(roomId, response.getRoomId());

        verify(availableTimeRepository, times(1)).save(any());
        verify(dailyAvailableTimeRepository, times(1)).saveAll(any());
    }

    @Test
    void 예약존재하면_업데이트실패() {
        int memberId = 1, orgId = 10, atId = 5;

        Organization org = new Organization();

        when(organizationRepository.findById(orgId))
                .thenReturn(Optional.of(org));

        when(organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, org, OrganizationRole.ADMIN))
                .thenReturn(true);

        AvailableTime at = mock(AvailableTime.class);
        when(availableTimeRepository.findById(atId)).thenReturn(Optional.of(at));

        // 이미 예약 존재
        when(dailyReservationRepository.existsByDailyAvailableTime_AvailableTime(at))
                .thenReturn(true);

        AvailableTimeRequestDTO dto = new AvailableTimeRequestDTO();

        assertThrows(ReservationExistsException.class, () -> {
            availableTimeService.updateAvailableTime(memberId, orgId, atId, dto);
        });
    }



    @Test
    void 예약가능시간_업데이트_성공() {

        int memberId = 1, orgId = 10, atId = 5;

        Organization org = new Organization();
        Room room = Room.builder().id(99).organization(org).build();

        AvailableTime at = AvailableTime.create(
                true,false,false,false,false,false,false,
                "오전타임", "09:00", "12:00", 1, "09:00",
                "2025-11-01","2025-11-02",60,
                room
        );

        AvailableTimeRequestDTO dto = new AvailableTimeRequestDTO();
        dto.setName("수정된 오전타임");
        dto.setMon(true);
        dto.setAvailableStartTime("10:00");
        dto.setAvailableEndTime("12:00");
        dto.setRepeatStartDay("2025-11-01");
        dto.setRepeatEndDay("2025-11-02");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));

        when(organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, org, OrganizationRole.ADMIN))
                .thenReturn(true);

        when(availableTimeRepository.findById(atId)).thenReturn(Optional.of(at));

        when(dailyReservationRepository.existsByDailyAvailableTime_AvailableTime(at)).thenReturn(false);

        when(availableTimeRepository.findAllByRoomId(room.getId())).thenReturn(List.of());

        when(availableTimeRepository.save(any())).thenReturn(at);

        AvailableTimeResponseDTO result =
                availableTimeService.updateAvailableTime(memberId, orgId, atId, dto);

        assertEquals("수정된 오전타임", result.getName());

        verify(dailyAvailableTimeRepository, times(1)).deleteAllByAvailableTime(at);
        verify(dailyAvailableTimeRepository, times(1)).saveAll(any());
    }

    @Test
    void 예약있으면_삭제실패() {
        int memberId = 1, orgId = 10, atId = 5;

        Organization org = new Organization();
        AvailableTime at = mock(AvailableTime.class);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, org, OrganizationRole.ADMIN)).thenReturn(true);

        when(availableTimeRepository.findById(atId)).thenReturn(Optional.of(at));
        when(dailyReservationRepository.existsByDailyAvailableTime_AvailableTime(at)).thenReturn(true);

        assertThrows(ReservationExistsException.class,
                () -> availableTimeService.deleteAvailableTime(memberId, orgId, atId));
    }


    @Test
    void 예약없으면_삭제성공() {
        int memberId = 1, orgId = 10, atId = 5;

        Organization org = new Organization();
        AvailableTime at = mock(AvailableTime.class);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, org, OrganizationRole.ADMIN)).thenReturn(true);
        when(availableTimeRepository.findById(atId)).thenReturn(Optional.of(at));
        when(dailyReservationRepository.existsByDailyAvailableTime_AvailableTime(at)).thenReturn(false);

        availableTimeService.deleteAvailableTime(memberId, orgId, atId);

        verify(dailyAvailableTimeRepository, times(1)).deleteAllByAvailableTime(at);
        verify(availableTimeRepository, times(1)).delete(at);
    }
}
