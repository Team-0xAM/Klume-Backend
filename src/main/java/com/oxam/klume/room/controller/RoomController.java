package com.oxam.klume.room.controller;

import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Tag(name = "Room - Room CRUD", description = "회의실 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/rooms")
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "조직 내 회의실 목록 조회",
            description = "조직 내 회의실 목록(이름, 사진, 수용 인원 등)을 조회한다." )
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getRooms(@PathVariable int organizationId) {

        // 테스트용 사용자ID
        int memberId = 5;
        return ResponseEntity.ok(roomService.getRooms(organizationId, memberId));
    }

    @Operation(
            summary = "조직 내 회의실 상세 조회",
            description = "특정 회의실의 세부정보를 조회할 수 있다." )
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> getRoomDetail(
            @PathVariable int organizationId,
            @PathVariable int roomId) {

        // 테스트용 사용자ID
        int memberId = 5;
        return ResponseEntity.ok(roomService.getRoomDetail(organizationId, roomId, memberId));
    }

    @Operation(
            summary = "조직 내 회의실 등록",
            description = "조직관리자가 새로운 회의실을 등록할 수 있다." )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomResponseDTO> createRoom(
            @PathVariable int organizationId,
            @ModelAttribute RoomRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // 테스트용 사용자ID
        int memberId = 5;
        RoomResponseDTO response = roomService.createRoomWithImage(organizationId, dto, imageFile, memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "조직 내 회의실 수정",
            description = "조직관리자가 회의실 정보를 수정할 수 있다." )
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId,
            @RequestBody RoomRequestDTO dto) {

        // 테스트용 사용자ID
        int memberId = 5;
        return ResponseEntity.ok(roomService.updateRoom(organizationId, roomId, dto, memberId));
    }

    @Operation(
            summary = "조직 내 회의실 삭제",
            description = "조직관리자가 해당 조직의 특정 회의실을 삭제할 수 있다.(이미 예약이 존재할 경우 삭제 불가)" )
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {

        // 테스트용 사용자ID
        int memberId = 5;
        roomService.deleteRoom(organizationId, roomId, memberId);
        return ResponseEntity.noContent().build();
    }
}
