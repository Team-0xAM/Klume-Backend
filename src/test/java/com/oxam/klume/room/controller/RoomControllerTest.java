package com.oxam.klume.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    private MockMvc mockMvc;

    private RoomService roomService;
    private MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // 순수 Mockito mock
        roomService = Mockito.mock(RoomService.class);
        memberService = Mockito.mock(MemberService.class);

        // 컨트롤러를 직접 주입해서 standalone MockMvc 구성
        RoomController controller = new RoomController(memberService, roomService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Authentication mockAuth(String email) {
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(email);
        Mockito.when(auth.getName()).thenReturn(email);
        return auth;
    }

    private Member mockMember(int id, String email) {
        Member m = Mockito.mock(Member.class);
        Mockito.when(m.getId()).thenReturn(id);
        Mockito.when(m.getEmail()).thenReturn(email);
        return m;
    }

    @Test
    @DisplayName("회의실 목록 조회 성공")
    void testGetRooms() throws Exception {
        Authentication auth = mockAuth("user@test.com");
        Member member = mockMember(1, "user@test.com");

        Mockito.when(memberService.findMemberByEmail(anyString())).thenReturn(member);
        Mockito.when(roomService.getRooms(eq(10), eq(1)))
                .thenReturn(List.of(RoomResponseDTO.builder()
                        .id(1).name("3층회의실").capacity(10).organizationId(10).build()));

        mockMvc.perform(get("/organizations/10/rooms").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("3층회의실"));
    }

    @Test
    @DisplayName("회의실 상세 조회 성공")
    void testGetRoomDetail() throws Exception {
        Authentication auth = mockAuth("user@test.com");
        Member member = mockMember(1, "user@test.com");

        Mockito.when(memberService.findMemberByEmail(anyString())).thenReturn(member);
        Mockito.when(roomService.getRoomDetail(eq(10), eq(5), eq(1)))
                .thenReturn(RoomResponseDTO.builder()
                        .id(5).name("4층회의실").capacity(8).organizationId(10).build());

        mockMvc.perform(get("/organizations/10/rooms/5").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("4층회의실"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("회의실 생성 성공 (multipart 업로드 포함)")
    void testCreateRoom() throws Exception {
        Authentication auth = mockAuth("admin@test.com");
        Member admin = mockMember(99, "admin@test.com");

        Mockito.when(memberService.findMemberByEmail(anyString())).thenReturn(admin);

        RoomRequestDTO dto = new RoomRequestDTO("3층회의실", "설명", 10);

        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto", "", "application/json",
                objectMapper.writeValueAsString(dto).getBytes()
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "room.jpg", "image/jpeg", "room_image".getBytes()
        );

        Mockito.when(roomService.createRoomWithImage(anyInt(), any(RoomRequestDTO.class), any(), anyInt()))
                .thenReturn(RoomResponseDTO.builder()
                        .id(1).name("3층회의실").capacity(10).organizationId(10).build());

        mockMvc.perform(multipart("/organizations/10/rooms")
                        .file(dtoFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("3층회의실"));
    }

    @Test
    @DisplayName("회의실 수정 성공")
    void testUpdateRoom() throws Exception {
        Authentication auth = mockAuth("admin@test.com");
        Member admin = mockMember(101, "admin@test.com");

        Mockito.when(memberService.findMemberByEmail(anyString())).thenReturn(admin);

        RoomRequestDTO dto = new RoomRequestDTO("수정된 3층회의실", "변경된 설명", 12);

        Mockito.when(roomService.updateRoom(eq(10), eq(5), any(RoomRequestDTO.class), eq(101)))
                .thenReturn(RoomResponseDTO.builder()
                        .id(5).name("수정된 3층회의실").capacity(12).organizationId(10).build());

        mockMvc.perform(put("/organizations/10/rooms/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 3층회의실"));
    }

    @Test
    @DisplayName("회의실 삭제 성공")
    void testDeleteRoom() throws Exception {
        Authentication auth = mockAuth("admin@test.com");
        Member admin = mockMember(100, "admin@test.com");

        Mockito.when(memberService.findMemberByEmail(anyString())).thenReturn(admin);
        Mockito.doNothing().when(roomService).deleteRoom(eq(10), eq(99), eq(100));

        mockMvc.perform(delete("/organizations/10/rooms/99").principal(auth))
                .andExpect(status().isNoContent());
    }
}
