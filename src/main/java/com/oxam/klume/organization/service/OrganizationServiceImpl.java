package com.oxam.klume.organization.service;

import com.oxam.klume.file.FileValidator;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.exception.OrganizationNotAdminException;
import com.oxam.klume.organization.exception.OrganizationNotFoundException;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final String INVITATION_CODE_PREFIX = "inviteCode:";

    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    private final FileValidator fileValidator;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @Override
    public void createOrganization(final int memberId, final MultipartFile file, final OrganizationRequestDTO requestDTO) {
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
    }

    @Transactional(readOnly = true)
    @Override
    public String createInviteCode(final int organizationId, final int memberId) {
        final Member member = findMemberById(memberId);

        final Organization organization = findOrganizationById(organizationId);

        // 회원이 이미 조직에 가입되었는지 검증 (만약 가입했다면 이미 가입된 회원이라는 에러 발생)
        validateMemberNotInOrganization(memberId, organization);

        OrganizationGroup organizationGroup = null;

        final String inviteCode = createRandomCode(6);

        redisTemplate.opsForValue().set(INVITATION_CODE_PREFIX + inviteCode, String.valueOf(organization.getId()),
                Duration.ofMinutes(30));

        return inviteCode;
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
        final String organizationId = redisService.getData(INVITATION_CODE_PREFIX + code);

        if (organizationId == null) {
            throw new OrganizationInvitationCodeInvalidException();
        }

        return Integer.parseInt(organizationId);
    }

    private void saveInvitationCodeToRedis(final int organizationId, final String invitationCode) {
        final String value = redisService.getData(ORGANIZATION_PREFIX + organizationId);

        if (value != null) {
            redisService.deleteData(INVITATION_CODE_PREFIX + value);
        }

        redisService.set(INVITATION_CODE_PREFIX + invitationCode, String.valueOf(organizationId), Duration.ofMinutes(30));
        redisService.set(ORGANIZATION_PREFIX + organizationId, invitationCode, Duration.ofMinutes(30));
    }

    private void validateAdminPermission(final int memberId, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberIdAndOrganizationAndRole(memberId, organization, role)) {
            throw new OrganizationNotAdminException();
        }
    }

    private String createRandomCode(final int length) {
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
}