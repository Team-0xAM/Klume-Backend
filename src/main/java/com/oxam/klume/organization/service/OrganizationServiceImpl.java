package com.oxam.klume.organization.service;

import com.oxam.klume.common.redis.RedisService;
import com.oxam.klume.file.FileValidator;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
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

    @Transactional
    @Override
    public String createInvitationCode(final int organizationId, final int memberId) {
        final Organization organization = findOrganizationById(organizationId);

        findOrganizationMemberByMemberIdAndOrganization(memberId, organization);

        validateAdminPermission(memberId, organization, OrganizationRole.ADMIN);

        final String invitationCode = generateRandomCode(6);

        saveInvitationCodeToRedis(organizationId, invitationCode);

        return invitationCode;
    }

    @Transactional(readOnly = true)
    @Override
    public OrganizationMember findOrganizationMemberRole(final int memberId, final int organizationId) {
        final Organization organization = findOrganizationById(organizationId);

        return findOrganizationMemberByMemberIdAndOrganization(memberId, organization);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrganizationGroup> findOrganizationGroups(int memberId, int organizationId) {
        final Organization organization = findOrganizationById(organizationId);

        final List<OrganizationGroup> organizationGroups = organizationGroupRepository.findByOrganization(organization);

        return organizationGroups;
    }

    @Transactional(readOnly = true)
    @Override
    public Organization validateInvitationCode(final int memberId, final String code) {
        final int organizationId = getOrganizationIdFromRedis(code);

        final Organization organization = findOrganizationById(organizationId);

        validateMemberNotInOrganization(memberId, organization);

        return organization;
    }

    @Transactional
    @Override
    public OrganizationMember createOrganizationMember(final int memberId, final int organizationId,
                                                       final OrganizationMemberRequestDTO requestDTO) {
        final Member member = findMemberById(memberId);

        final Organization organization = findOrganizationById(organizationId);

        validateMemberNotInOrganization(memberId, organization);

        OrganizationGroup organizationGroup = null;

        if (requestDTO.getOrganizationGroupId() != null) {
            organizationGroup = findOrganizationGroupById(requestDTO.getOrganizationGroupId());
        }

        final OrganizationMember organizationMember = new OrganizationMember(OrganizationRole.MEMBER,
                requestDTO.getNickname(), organization, member, organizationGroup);

        return organizationMemberRepository.save(organizationMember);
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