package com.oxam.klume.organization.service;

import com.oxam.klume.common.redis.RedisService;
import com.oxam.klume.file.FileValidator;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.dto.OrganizationGroupResponseDTO;
import com.oxam.klume.organization.dto.OrganizationMemberRequestDTO;
import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationGroup;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.*;
import com.oxam.klume.organization.repository.OrganizationGroupRepository;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final String INVITATION_CODE_PREFIX = "inviteCode:";
    private static final String ORGANIZATION_PREFIX = "organization:";

    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationGroupRepository organizationGroupRepository;

    private final FileValidator fileValidator;
    private final RedisService redisService;

    /**
     * 조직 생성
     *
     * @param memberId   로그인한 회원의 id
     * @param file       조직 이미지 파일
     * @param requestDTO 조직 정보
     */
    @Transactional
    @Override
    public Organization createOrganization(final int memberId, final MultipartFile file, final OrganizationRequestDTO requestDTO) {
        final Member member = findMemberById(memberId);

        final String imageUrl = uploadImage(file);

        Organization organization = new Organization(requestDTO.getName(), requestDTO.getDescription(), imageUrl);

        organization = organizationRepository.save(organization);

        final OrganizationMember organizationMember = OrganizationMember.builder()
                .member(member)
                .role(OrganizationRole.ADMIN)
                .nickname(requestDTO.getNickname())
                .organization(organization)
                .build();

        organizationMemberRepository.save(organizationMember);

        return organization;
    }

    /**
     * 초대 코드 발급
     *
     * @param organizationId 조직 id
     * @param memberId       로그인한 회원의 id
     * @return 초대 코드
     */
    @Transactional
    @Override
    public String createInvitationCode(final int organizationId, final int memberId) {
        // 1) 조직 id로 조직 찾기
        final Organization organization = findOrganizationById(organizationId);

        // 2) 로그인한 회원이 조직 멤버인지 확인
        findOrganizationMemberByMemberIdAndOrganization(memberId, organization);

        // 3) 조직 내 권한이 관리자인지 확인 (관리자만 초대 코드 발급 가능)
        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN);

        // 4) 초대 코드 생성
        final String invitationCode = generateRandomCode(6);

        // 5) 레디스에 조직 id, 초대 코드 저장
        saveInvitationCodeToRedis(organizationId, invitationCode);

        return invitationCode;
    }

    /**
     * 조직 내 회원 권한 조회
     *
     * @param memberId       로그인한 회원의 id
     * @param organizationId 조직의 id
     */
    @Transactional(readOnly = true)
    @Override
    public OrganizationMember findOrganizationMemberRole(final int memberId, final int organizationId) {
        final Organization organization = findOrganizationById(organizationId);

        return findOrganizationMemberByMemberIdAndOrganization(memberId, organization);
    }

    /**
     * 조직 id로 조직 그룹 목록 조회
     *
     * @param memberId       로그인한 회원의 id
     * @param organizationId 조직의 id
     */
    @Transactional(readOnly = true)
    @Override
    public List<OrganizationGroupResponseDTO> findOrganizationGroups(final int memberId, final int organizationId) {
        final Organization organization = findOrganizationById(organizationId);

        final List<OrganizationGroup> organizationGroups = organizationGroupRepository.findByOrganization(organization);

        final boolean isOrganizationAdmin = existsByMemberIdAndOrganizationAndRole(memberId, organization, OrganizationRole.ADMIN);

        return organizationGroups.stream()
                .map(group -> {
                    Integer memberCount = null;

                    if (isOrganizationAdmin) {
                        memberCount = countByOrganizationAndOrganizationGroup(organization, group);
                    }

                    return OrganizationGroupResponseDTO.of(group, memberCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 초대 코드 검증
     *
     * @param memberId 로그인한 회원의 id
     * @param code     초대 코드
     */
    @Transactional(readOnly = true)
    @Override
    public Organization validateInvitationCode(final int memberId, final String code) {
        // 1) 레디스에서 초대 코드를 키로 가지는 데이터 조회 (만약 레디스에 없을 경우 만료되었거나 유효하지 않는 코드라는 에러 발생)
        final int organizationId = getOrganizationIdFromRedis(code);

        // 2) 조직 id로 조직 조회
        final Organization organization = findOrganizationById(organizationId);

        // 3) 회원이 이미 조직에 가입되었는지 검증 (만약 가입했다면 이미 가입된 회원이라는 에러 발생)
        validateMemberNotInOrganization(memberId, organization);

        return organization;
    }

    /**
     * 조직 가입
     *
     * @param memberId       로그인한 회원의 id
     * @param organizationId 조직 id
     * @param requestDTO     조직에서 사용할 닉네임 및 그룹 정보
     * @return
     */
    @Transactional
    @Override
    public OrganizationMember createOrganizationMember(final int memberId, final int organizationId,
                                                       final OrganizationMemberRequestDTO requestDTO) {
        final Member member = findMemberById(memberId);

        final Organization organization = findOrganizationById(organizationId);

        // 회원이 이미 조직에 가입되었는지 검증 (만약 가입했다면 이미 가입된 회원이라는 에러 발생)
        validateMemberNotInOrganization(memberId, organization);

        OrganizationGroup organizationGroup = null;

        if (requestDTO.getOrganizationGroupId() != null) {
            organizationGroup = findOrganizationGroupById(requestDTO.getOrganizationGroupId());
        }

        final OrganizationMember organizationMember = new OrganizationMember(OrganizationRole.MEMBER,
                requestDTO.getNickname(), organization, member, organizationGroup);

        return organizationMemberRepository.save(organizationMember);
    }

    private int countByOrganizationAndOrganizationGroup(final Organization organization,
                                                        final OrganizationGroup organizationGroup) {
        return organizationMemberRepository.countByOrganizationAndOrganizationGroup(organization, organizationGroup);
    }

    private void validateMemberNotInOrganization(final int memberId, final Organization organization) {
        if (organizationMemberRepository.findByMemberIdAndOrganization(memberId, organization).isPresent()) {
            throw new OrganizationMemberAlreadyExistsException();
        }
    }

    private int getOrganizationIdFromRedis(final String code) {
        final String organizationId = redisService.get(INVITATION_CODE_PREFIX + code);

        if (organizationId == null) {
            throw new OrganizationInvitationCodeInvalidException();
        }

        return Integer.parseInt(organizationId);
    }

    private void saveInvitationCodeToRedis(final int organizationId, final String invitationCode) {
        final String value = redisService.get(ORGANIZATION_PREFIX + organizationId);

        if (value != null) {
            redisService.delete(INVITATION_CODE_PREFIX + value);
        }

        redisService.set(INVITATION_CODE_PREFIX + invitationCode, String.valueOf(organizationId), Duration.ofMinutes(30));
        redisService.set(ORGANIZATION_PREFIX + organizationId, invitationCode, Duration.ofMinutes(30));
    }

    private void validateAdminPermission(final int memberId, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role)) {
            throw new OrganizationNotAdminException();
        }
    }

    private boolean existsByMemberIdAndOrganizationAndRole(final int memberId, final Organization organization,
                                                           final OrganizationRole role) {
        return organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role);
    }

    private String generateRandomCode(final int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        final SecureRandom secureRandom = new SecureRandom();
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }

    private String uploadImage(final MultipartFile file) {
        if (file != null) {
            fileValidator.validateImage(file);

            // TODO s3 업로드 후 이미지 url 반환
            return "https://~";
        }
        return null;
    }

    private Member findMemberById(final int memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    private Organization findOrganizationById(final int organizationId) {
        return organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
    }

    private OrganizationMember findOrganizationMemberByMemberIdAndOrganization(final int memberId,
                                                                               final Organization organization) {
        return organizationMemberRepository.findByMemberIdAndOrganization(memberId, organization)
                .orElseThrow(OrganizationMemberAccessDeniedException::new);
    }

    private OrganizationGroup findOrganizationGroupById(final int groupId) {
        return organizationGroupRepository.findById(groupId).orElseThrow(OrganizationGroupNotFoundException::new);
    }
}