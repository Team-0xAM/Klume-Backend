package com.oxam.klume.room.service;

import com.oxam.klume.file.FileValidator;
import com.oxam.klume.file.infra.S3Uploader;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationMemberAccessDeniedException;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.exception.RoomNameDuplicationException;
import com.oxam.klume.room.repository.RoomRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class RoomServiceImplTest {

    private final RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
    private final OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);
    private final OrganizationMemberRepository organizationMemberRepository = Mockito.mock(OrganizationMemberRepository.class);
    private final FileValidator fileValidator = Mockito.mock(FileValidator.class);
    private final S3Uploader s3Uploader = Mockito.mock(S3Uploader.class);

    private final RoomServiceImpl roomService =
            new RoomServiceImpl(roomRepository, organizationRepository, organizationMemberRepository,
                    fileValidator, s3Uploader);

    @Test
    @DisplayName("회의실 생성 성공")
    void createRoomSuccess() {
        Organization org = new Organization(
                "한화시스템Beyond",
                "개발자 과정입니다",
                null
        );
        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        RoomRequestDTO dto = new RoomRequestDTO("3층회의실", "3층회의실입니다", 10);

        MultipartFile image = Mockito.mock(MultipartFile.class);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.existsByOrganizationAndName(org, "3층회의실")).thenReturn(false);
        Mockito.when(s3Uploader.upload(anyString(), any())).thenReturn("https://s3.../roomA.jpg");

        Room saved = Room.builder()
                .id(1)
                .name("3층회의실")
                .capacity(10)
                .organization(org)
                .imageUrl("https://s3.../roomA.jpg")
                .build();

        Mockito.when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomResponseDTO result = roomService.createRoomWithImage(10, dto, image, 1);

        assertEquals("3층회의실", result.getName());
        assertEquals(10, result.getCapacity());
//        assertEquals(10, result.getOrganizationId());
    }

    @Test
    @DisplayName("회의실 생성 실패 — 중복 이름")
    void createRoomFailDuplicateName() {
        Organization org = new Organization(
                "한화시스템Beyond",
                "개발자 과정입니다",
                null
        );
        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.existsByOrganizationAndName(org, "3층회의실")).thenReturn(true);

        RoomRequestDTO dto = new RoomRequestDTO("3층회의실", "3층에 있는 회의실입니다", 10);

        assertThrows(RoomNameDuplicationException.class, () ->
                roomService.createRoomWithImage(10, dto, null, 1)
        );
    }

    @Test
    @DisplayName("회의실 생성 실패 — 관리자가 아님")
    void createRoomFailNotAdmin() {
        Organization org = new Organization(
                "한화시스템Beyond",
                "개발자 과정입니다",
                null
        );
        OrganizationMember member = Mockito.mock(OrganizationMember.class);
        Mockito.when(member.getRole()).thenReturn(OrganizationRole.MEMBER);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(member));

        RoomRequestDTO dto = new RoomRequestDTO("3층회의실", "3층에있는 회의실입니다", 10);

        assertThrows(OrganizationNotAdminException.class, () ->
                roomService.createRoomWithImage(10, dto, null, 1)
        );
    }

    @Test
    @DisplayName("회의실 상세 조회 성공 — 조직원 접근")
    void getRoomDetailSuccess() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember member = Mockito.mock(OrganizationMember.class);
        Mockito.when(member.isBanned()).thenReturn(false);

        Room room = Room.builder()
                .id(11)
                .name("A-회의실")
                .description("A설명")
                .capacity(8)
                .organization(org)
                .imageUrl("https://s3.../a.jpg")
                .build();

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(member));
        Mockito.when(roomRepository.findById(11)).thenReturn(Optional.of(room));

        RoomResponseDTO dto = roomService.getRoomDetail(10, 11, 1);

        assertEquals("A-회의실", dto.getName());
        assertEquals(8, dto.getCapacity());
        assertEquals("https://s3.../a.jpg", dto.getImageUrl());
    }

    @Test
    @DisplayName("회의실 목록 조회 성공 — 조직원 접근")
    void getRoomsSuccess() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember member = Mockito.mock(OrganizationMember.class);
        Mockito.when(member.isBanned()).thenReturn(false);

        Room r1 = Room.builder().id(1).name("A").capacity(4).organization(org).build();
        Room r2 = Room.builder().id(2).name("B").capacity(6).organization(org).build();

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(member));
        Mockito.when(roomRepository.findByOrganization(org)).thenReturn(java.util.List.of(r1, r2));

        var list = roomService.getRooms(10, 1);

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> r.getName().equals("A")));
        assertTrue(list.stream().anyMatch(r -> r.getName().equals("B")));
    }

    @Test
    @DisplayName("회의실 상세 조회 실패 — 조직원이 아님")
    void getRoomDetailFailNotMember() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 999))
                .thenReturn(Optional.empty());

        assertThrows(OrganizationMemberAccessDeniedException.class, () ->
                roomService.getRoomDetail(10, 11, 999)
        );
    }

    @Test
    @DisplayName("회의실 상세/목록 조회 실패 — 정지된 사용자")
    void getRoomFailBannedMember() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember banned = Mockito.mock(OrganizationMember.class);
        Mockito.when(banned.isBanned()).thenReturn(true);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(banned));

        // 목록 조회도, 상세 조회도 동일하게 OrganizationNotAdminException 던지도록 구현되어 있음
        assertThrows(OrganizationNotAdminException.class, () ->
                roomService.getRooms(10, 1)
        );
        assertThrows(OrganizationNotAdminException.class, () ->
                roomService.getRoomDetail(10, 11, 1)
        );
    }

    @Test
    @DisplayName("회의실 수정 성공 — 관리자")
    void updateRoomSuccess() {
        Organization org = Mockito.spy(new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null));
        Mockito.when(org.getId()).thenReturn(10); // ID 스텁

        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Room room = Room.builder()
                .id(11)
                .name("수정전")
                .description("desc")
                .capacity(4)
                .organization(org)
                .build();

        RoomRequestDTO dto = new RoomRequestDTO("수정후", "바뀐설명", 12);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.findById(11)).thenReturn(Optional.of(room));

        RoomResponseDTO result = roomService.updateRoom(10, 11, dto, 1);

        assertEquals("수정후", result.getName());
        assertEquals(12, result.getCapacity());
        assertEquals("바뀐설명", result.getDescription());
    }

    @Test
    @DisplayName("회의실 수정 실패 — 관리자가 아님")
    void updateRoomFailNotAdmin() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember member = Mockito.mock(OrganizationMember.class);
        Mockito.when(member.getRole()).thenReturn(OrganizationRole.MEMBER);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(member));

        RoomRequestDTO dto = new RoomRequestDTO("x", "y", 3);

        assertThrows(OrganizationNotAdminException.class, () ->
                roomService.updateRoom(10, 11, dto, 1)
        );
    }

    @Test
    @DisplayName("회의실 수정 실패 — 수용 인원 0 이하")
    void updateRoomFailInvalidCapacity() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Room room = Room.builder()
                .id(11).name("A").description("d").capacity(4).organization(org).build();

        RoomRequestDTO dto = new RoomRequestDTO("A", "d", 0);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.findById(11)).thenReturn(Optional.of(room));

        assertThrows(IllegalArgumentException.class, () ->
                roomService.updateRoom(10, 11, dto, 1)
        );
    }

    @Test
    @DisplayName("회의실 삭제 성공 — 관리자")
    void deleteRoomSuccess() {
        Organization org = Mockito.spy(new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null));
        Mockito.when(org.getId()).thenReturn(10);

        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Room room = Room.builder()
                .id(11).name("A").capacity(4).organization(org).build();

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.findById(11)).thenReturn(Optional.of(room));

        roomService.deleteRoom(10, 11, 1);

        Mockito.verify(roomRepository, Mockito.times(1)).delete(room);
    }

    @Test
    @DisplayName("회의실 삭제 실패 — 관리자가 아님")
    void deleteRoomFailNotAdmin() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember member = Mockito.mock(OrganizationMember.class);
        Mockito.when(member.getRole()).thenReturn(OrganizationRole.MEMBER);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(member));

        assertThrows(OrganizationNotAdminException.class, () ->
                roomService.deleteRoom(10, 11, 1)
        );
    }

    @Test
    @DisplayName("회의실 조회/수정/삭제 실패 — 다른 조직의 회의실 (조직 불일치)")
    void roomOrganizationMismatch() {
        // org(10) 로 요청하지만, room 은 orgB(77)에 속함 → 불일치 예외
        Organization reqOrg = Mockito.spy(new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null));
        Mockito.when(reqOrg.getId()).thenReturn(10);

        Organization orgB = Mockito.spy(new Organization("한화시스템BeyondSPY",
                "개발자 과정아닙니다",
                null));
        Mockito.when(orgB.getId()).thenReturn(77);

        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Room fakeRoom = Mockito.mock(Room.class);
        Mockito.when(fakeRoom.getOrganization()).thenReturn(orgB);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(reqOrg));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.findById(11)).thenReturn(Optional.of(fakeRoom));

        // 상세 조회 경로에서 getRoomOrThrow 호출되며 불일치 예외 발생
        assertThrows(IllegalArgumentException.class, () ->
                roomService.getRoomDetail(10, 11, 1)
        );

        // 수정/삭제도 동일하게 불일치 예외
        RoomRequestDTO dto = new RoomRequestDTO("x", "y", 3);
        assertThrows(IllegalArgumentException.class, () ->
                roomService.updateRoom(10, 11, dto, 1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                roomService.deleteRoom(10, 11, 1)
        );
    }

    @Test
    @DisplayName("회의실 생성 실패 — 수용 인원 0 이하")
    void createRoomFailInvalidCapacity() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.existsByOrganizationAndName(org, "X")).thenReturn(false);

        RoomRequestDTO dto = new RoomRequestDTO("X", "Y", 0);

        assertThrows(com.oxam.klume.room.exception.RoomCapacityInvalidException.class, () ->
                roomService.createRoomWithImage(10, dto, null, 1)
        );
    }

    @Test
    @DisplayName("회의실 생성 성공 — 이미지 없이 생성(업로드 호출 안 됨)")
    void createRoomWithoutImageSuccess() {
        Organization org = new Organization("한화시스템Beyond",
                "개발자 과정입니다",
                null);
        OrganizationMember admin = Mockito.mock(OrganizationMember.class);
        Mockito.when(admin.getRole()).thenReturn(OrganizationRole.ADMIN);

        Mockito.when(organizationRepository.findById(10)).thenReturn(Optional.of(org));
        Mockito.when(organizationMemberRepository.findByOrganizationIdAndMemberId(10, 1))
                .thenReturn(Optional.of(admin));
        Mockito.when(roomRepository.existsByOrganizationAndName(org, "A")).thenReturn(false);

        Room saved = Room.builder()
                .id(100)
                .name("A")
                .description("d")
                .capacity(5)
                .organization(org)
                .imageUrl(null)
                .build();

        Mockito.when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomRequestDTO dto = new RoomRequestDTO("A", "d", 5);
        RoomResponseDTO res = roomService.createRoomWithImage(10, dto, null, 1);

        assertEquals("A", res.getName());
        assertNull(res.getImageUrl());

        // 업로더는 호출되지 않아야 함
        Mockito.verifyNoInteractions(fileValidator);
        Mockito.verify(s3Uploader, Mockito.never()).upload(anyString(), any());
    }

}
