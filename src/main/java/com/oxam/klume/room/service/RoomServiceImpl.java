package com.oxam.klume.room.service;

import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import com.oxam.klume.room.dto.RoomRequestDTO;
import com.oxam.klume.room.dto.RoomResponseDTO;
import com.oxam.klume.room.entity.Room;
import com.oxam.klume.room.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    // 회의실 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRooms(int organizationId, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        findOrganizationMemberById(organizationId, memberId);
        return roomRepository.findByOrganization(organization)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 회의실 상세 조회
    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomDetail(int organizationId, int roomId, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        findOrganizationMemberById(organizationId, memberId);
        Room room = getRoomOrThrow(roomId, organization);
        return toResponseDTO(room);
    }

    // 회의실 + 이미지 등록
    @Override
    public RoomResponseDTO createRoomWithImage(int organizationId, RoomRequestDTO dto, MultipartFile imageFile, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);

        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        if (member.getRole() != OrganizationRole.ADMIN) {
            throw new OrganizationNotAdminException("회의실을 등록할 권한이 없습니다.");
        }

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

        room.assignToOrganization(organization);
        room.validateCapacity();

        Room saved = roomRepository.save(room);
        return toResponseDTO(saved);
    }

    // 회의실 수정
    @Override
    public RoomResponseDTO updateRoom(int organizationId, int roomId, RoomRequestDTO dto, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        if (member.getRole() != OrganizationRole.ADMIN) {
            throw new OrganizationNotAdminException("회의실을 수정할 권한이 없습니다.");
        }

        Room room = getRoomOrThrow(roomId, organization);
        room.updateInfo(dto.getName(), dto.getDescription(), dto.getCapacity());
        room.validateCapacity();

        return toResponseDTO(room);
    }

    // 회의실 삭제
    @Override
    public void deleteRoom(int organizationId, int roomId, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        if (member.getRole() != OrganizationRole.ADMIN) {
            throw new OrganizationNotAdminException("회의실을 삭제할 권한이 없습니다.");
        }

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

    // 조직에 포함된 회원인지 검증 + 정지 여부 추가
    private OrganizationMember findOrganizationMemberById(int organizationId, int memberId) {
        OrganizationMember member = organizationMemberRepository
                .findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new OrganizationNotFoundException("사용자가 가입하지 않은 조직입니다."));

        // 필요 시 정지 상태 등 추가 검증 가능
        // if (member.isBanned()) throw new OrganizationNotAdminException("정지된 사용자는 접근할 수 없습니다.");

        return member;
    }

    // 조직 존재 여부 확인
    private Organization getOrganizationOrThrow(int organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("조직을 찾을 수 없습니다."));
    }

    // 회의실 존재 여부 확인 + 조직 일치 검증 추가
    private Room getRoomOrThrow(int roomId, Organization organization) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("회의실을 찾을 수 없습니다."));

        if (!Objects.equals(room.getOrganization().getId(), organization.getId())) {
            throw new IllegalArgumentException("해당 조직에 속한 회의실이 아닙니다.");
        }
        return room;
    }

    // 이미지 업로드 (S3 연동 전, mock URL 반환)
    private String uploadImage(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return "https://s3.amazonaws.com/klume-bucket/" + file.getOriginalFilename();
        }
        return null;
    }
}
