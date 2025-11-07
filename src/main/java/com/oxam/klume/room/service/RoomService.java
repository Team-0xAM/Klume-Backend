package com.oxam.klume.room.service;

import com.oxam.klume.common.error.exception.EntityNotFoundException;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
    private final RoomRepository roomRepository;
    private final OrganizationRepository organizationRepository;

    public RoomResponseDTO createRoom(int organizationId, RoomRequestDTO dto) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException());

        if (roomRepository.existsByOrganizationAndName(organization, dto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 회의실이 존재합니다.");
        }

        Room room = Room.builder()
                .organization(organization)
                .name(dto.getName())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .imageUrl(dto.getImageUrl())
                .build();

        Room saved = roomRepository.save(room);

        return RoomResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .capacity(saved.getCapacity())
                .imageUrl(saved.getImageUrl())
                .organizationId(organizationId)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRooms(int organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("조직을 찾을 수 없습니다."));

        return roomRepository.findByOrganization(organization)
                .stream()
                .map(room -> RoomResponseDTO.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .description(room.getDescription())
                        .capacity(room.getCapacity())
                        .imageUrl(room.getImageUrl())
                        .organizationId(organizationId)
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomDetail(int organizationId, int roomId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("조직을 찾을 수 없습니다."));

        Room room = roomRepository.findByIdAndOrganization(roomId, organization)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다."));

        return RoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .imageUrl(room.getImageUrl())
                .organizationId(organizationId)
                .build();
    }

    public RoomResponseDTO updateRoom(int organizationId, int roomId, RoomRequestDTO dto) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("조직을 찾을 수 없습니다."));

        Room room = roomRepository.findByIdAndOrganization(roomId, organization)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다."));

        room.setName(dto.getName());
        room.setDescription(dto.getDescription());
        room.setCapacity(dto.getCapacity());
        room.setImageUrl(dto.getImageUrl());

        return RoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .imageUrl(room.getImageUrl())
                .organizationId(organizationId)
                .build();
    }

    public void deleteRoom(int organizationId, int roomId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException());

        Room room = roomRepository.findByIdAndOrganization(roomId, organization)
                .orElseThrow(() -> new EntityNotFoundException());

        // TODO: 예약 존재 여부 확인 로직 추가 예정 (ReservationRepository)
        roomRepository.delete(room);
    }
}
