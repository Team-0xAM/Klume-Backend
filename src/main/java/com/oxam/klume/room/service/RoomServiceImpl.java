package com.oxam.klume.room.service;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final OrganizationRepository organizationRepository;

    // 조직 존재 여부 확인
    private Organization getOrganizationOrThrow(int organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("조직을 찾을 수 없습니다."));
    }

    // 회의실 존재 여부 확인
    private Room getRoomOrThrow(int roomId, Organization organization) {
        return roomRepository.findByIdAndOrganization(roomId, organization)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다."));
    }

    // 이미지 업로드 (S3 연동 전, mock URL 반환)
    private String uploadImage(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            // TODO: S3 업로드 로직으로 교체
            return "https://s3.amazonaws.com/klume-bucket/" + file.getOriginalFilename();
        }
        return null;
    }

    // 회의실 + 이미지 등록
    @Override
    public RoomResponseDTO createRoomWithImage(int organizationId, RoomRequestDTO dto, MultipartFile imageFile) {
        Organization organization = getOrganizationOrThrow(organizationId);

        if (roomRepository.existsByOrganizationAndName(organization, dto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 회의실이 존재합니다.");
        }

        String imageUrl = uploadImage(imageFile);

        Room room = Room.builder()
                .organization(organization)
                .name(dto.getName())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .imageUrl(imageUrl)
                .build();

        Room saved = roomRepository.save(room);
        return toResponseDTO(saved);
    }

    // 회의실 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRooms(int organizationId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        return roomRepository.findByOrganization(organization)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 회의실 상세 조회
    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomDetail(int organizationId, int roomId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        Room room = getRoomOrThrow(roomId, organization);
        return toResponseDTO(room);
    }

    // 회의실 수정
    @Override
    public RoomResponseDTO updateRoom(int organizationId, int roomId, RoomRequestDTO dto) {
        Organization organization = getOrganizationOrThrow(organizationId);
        Room room = getRoomOrThrow(roomId, organization);

        room.setName(dto.getName());
        room.setDescription(dto.getDescription());
        room.setCapacity(dto.getCapacity());
//        room.setImageUrl(dto.getImageUrl());

        return toResponseDTO(room);
    }

    // 회의실 삭제
    @Override
    public void deleteRoom(int organizationId, int roomId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        Room room = getRoomOrThrow(roomId, organization);
        roomRepository.delete(room);
    }

    // 공통 변환 메서드
    private RoomResponseDTO toResponseDTO(Room room) {
        return RoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .imageUrl(room.getImageUrl())
                .organizationId(room.getOrganization().getId())
                .build();
    }
}
