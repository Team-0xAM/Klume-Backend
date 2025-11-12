package com.oxam.klume.room.service;

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
import com.oxam.klume.room.exception.RoomCapacityInvalidException;
import com.oxam.klume.room.exception.RoomNameDuplicationException;
import com.oxam.klume.room.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.oxam.klume.file.FileValidator;
import com.oxam.klume.file.infra.S3Uploader;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    private final FileValidator fileValidator;
    private final S3Uploader s3Uploader;

    // 회의실 목록 조회
    @Override
    public List<RoomResponseDTO> getRooms(int organizationId, int memberId) {
        log.info("getRooms 호출 - organizationId: {}, memberId: {}", organizationId, memberId);
        Organization organization = getOrganizationOrThrow(organizationId);
        log.info("조직 찾음 - organization: {}", organization.getId());
        findOrganizationMemberById(organizationId, memberId);
        log.info("조직 회원 확인 완료");
        return roomRepository.findByOrganization(organization)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 회의실 상세 조회
    @Override
    public RoomResponseDTO getRoomDetail(int organizationId, int roomId, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        findOrganizationMemberById(organizationId, memberId);
        Room room = getRoomOrThrow(roomId, organization);
        return toResponseDTO(room);
    }

    // 회의실 + 이미지 등록
    @Override
    public RoomResponseDTO createRoomWithImage(int organizationId, RoomRequestDTO dto, MultipartFile imageFile, int memberId) {
        log.info("createRoomWithImage 호출 - organizationId: {}, memberId: {}, dto: {}", organizationId, memberId, dto);
        Organization organization = getOrganizationOrThrow(organizationId);
        log.info("조직 찾음 - organization: {}", organization.getId());

        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        log.info("조직 회원 찾음 - member role: {}", member.getRole());
        if (member.getRole() != OrganizationRole.ADMIN) {
            throw new OrganizationNotAdminException("회의실을 등록할 권한이 없습니다.");
        }

        // 회의실 이름 중복 체크
        if (roomRepository.existsByOrganizationAndName(organization, dto.getName())) {
            throw new RoomNameDuplicationException();
        }

        // 수용 인원 검증
        if (dto.getCapacity() <= 0) {
            throw new RoomCapacityInvalidException();
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

    // 회의실 수정
    @Override
    public RoomResponseDTO updateRoom(int organizationId, int roomId, RoomRequestDTO dto, int memberId) {
        Organization organization = getOrganizationOrThrow(organizationId);
        OrganizationMember member = findOrganizationMemberById(organizationId, memberId);
        if (member.getRole() != OrganizationRole.ADMIN) {
            throw new OrganizationNotAdminException("회의실을 수정할 권한이 없습니다.");
        }

        Room room = getRoomOrThrow(roomId, organization);

        if (dto.getCapacity() <= 0) {
            throw new IllegalArgumentException("수용 인원은 1명 이상이어야 합니다.");
        }

        room.updateInfo(dto.getName(), dto.getDescription(), dto.getCapacity());
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
        log.info("findOrganizationMemberById 호출 - organizationId: {}, memberId: {}", organizationId, memberId);
        OrganizationMember member = organizationMemberRepository
                .findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> {
                    log.error("조직 회원을 찾을 수 없음 - organizationId: {}, memberId: {}", organizationId, memberId);
                    return new OrganizationMemberAccessDeniedException("사용자가 가입하지 않은 조직입니다.");
                });

        log.info("조직 회원 찾음 - member: {}, role: {}, banned: {}", member.getId(), member.getRole(), member.isBanned());
        // 정지 상태
        if (member.isBanned()) throw new OrganizationNotAdminException("정지된 사용자는 접근할 수 없습니다.");

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

    // 이미지 업로드
    private String uploadImage(final MultipartFile file) {
        if (file != null) {
            fileValidator.validateImage(file);

            return s3Uploader.upload("room/", file);
        }
        return null;
    }
}
