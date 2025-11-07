package com.oxam.klume.room.controller;


import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(
            @PathVariable int organizationId,
            @RequestBody RoomRequestDTO dto) {
        return ResponseEntity.ok(roomService.createRoom(organizationId, dto));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getRooms(@PathVariable int organizationId) {
        return ResponseEntity.ok(roomService.getRooms(organizationId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> getRoomDetail(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        return ResponseEntity.ok(roomService.getRoomDetail(organizationId, roomId));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId,
            @RequestBody RoomRequestDTO dto) {
        return ResponseEntity.ok(roomService.updateRoom(organizationId, roomId, dto));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable int organizationId,
            @PathVariable int roomId) {
        roomService.deleteRoom(organizationId, roomId);
        return ResponseEntity.noContent().build();
    }
}
