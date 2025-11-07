package com.oxam.klume.organization.service;

import com.oxam.klume.common.redis.RedisService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final String INVITATION_CODE_PREFIX = "inviteCode:";
    private static final String ORGANIZATION_PREFIX = "organization:";

    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    private final FileValidator fileValidator;
    private final RedisService redisService;

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
    public String createInvitationCode(final int organizationId, final int memberId) {
        final Member member = findMemberById(memberId);

        final Organization organization = findOrganizationById(organizationId);

        validateAdminPermission(member, organization, OrganizationRole.ADMIN);

        final String invitationCode = generateRandomCode(6);

        saveInvitationCodeToRedis(organizationId, invitationCode);

        return invitationCode;
    }

    private void saveInvitationCodeToRedis(final int organizationId, final String invitationCode) {
        final String value = redisService.get(ORGANIZATION_PREFIX + organizationId);

        if (value != null) {
            redisService.delete(INVITATION_CODE_PREFIX + value);
        }

        redisService.set(INVITATION_CODE_PREFIX + invitationCode, String.valueOf(organizationId), Duration.ofMinutes(30));
        redisService.set(ORGANIZATION_PREFIX + organizationId, invitationCode, Duration.ofMinutes(30));
    }

    private void validateAdminPermission(final Member member, final Organization organization, final OrganizationRole role) {
        if (!organizationMemberRepository.existsByMemberAndOrganizationAndRole(member, organization, role)) {
            throw new OrganizationNotAdminException();
        }
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
}