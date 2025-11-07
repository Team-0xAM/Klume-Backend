package com.oxam.klume.room.service;

import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {

    RoomResponseDTO createRoomWithImage(int organizationId, RoomRequestDTO dto, MultipartFile imageFile);

    List<RoomResponseDTO> getRooms(int organizationId);

    RoomResponseDTO getRoomDetail(int organizationId, int roomId);

    RoomResponseDTO updateRoom(int organizationId, int roomId, RoomRequestDTO dto);

    void deleteRoom(int organizationId, int roomId);
}
