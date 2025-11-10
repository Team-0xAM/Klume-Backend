package com.oxam.klume.organization.service;

import com.oxam.klume.common.redis.RedisService;
import com.oxam.klume.file.FileValidator;
import com.oxam.klume.file.infra.S3Uploader;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.dto.*;
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
    private final S3Uploader s3Uploader;

    @Transactional
    @Override
    public Organization createOrganization(final Member member, final MultipartFile file, final OrganizationRequestDTO requestDTO) {
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
    public OrganizationMember createOrganizationMember(final Member member, final int organizationId,
                                                       final OrganizationMemberRequestDTO requestDTO) {
        final Organization organization = findOrganizationById(organizationId);

        validateMemberNotInOrganization(member.getId(), organization);

        OrganizationGroup organizationGroup = null;

        if (requestDTO.getOrganizationGroupId() != null) {
            organizationGroup = findOrganizationGroupById(requestDTO.getOrganizationGroupId());
        }

        final OrganizationMember organizationMember = new OrganizationMember(OrganizationRole.MEMBER,
                requestDTO.getNickname(), organization, member, organizationGroup);

        return organizationMemberRepository.save(organizationMember);
    }

    @Override
    public List<Organization> findOrganizationByMember(final Member member) {
        return organizationMemberRepository.findOrganizationByMember(member);
    }

    @Transactional
    @Override
    public OrganizationMember updateOrganizationMemberRole(final Member member, final int organizationMemberId,
                                                           final int organizationId, final OrganizationMemberRoleRequestDTO requestDTO) {
        final Organization organization = findOrganizationById(organizationId);

        validateAdminPermission(member.getId(), organization, OrganizationRole.ADMIN);

        final OrganizationMember organizationMember = organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(OrganizationMemberNotFoundException::new);

        organizationMember.updateRole(requestDTO.getOrganizationRole());

        if (requestDTO.getOrganizationRole() == OrganizationRole.ADMIN) {
            organizationMember.updateOrganizationGroup(null);
        }

        return organizationMember;
    }

    @Transactional
    @Override
    public OrganizationGroup createOrganizationGroup(final Member member, final int organizationId,
                                                     final OrganizationGroup organizationGroup) {
        final Organization organization = findOrganizationById(organizationId);

        findOrganizationMemberByMemberIdAndOrganization(member.getId(), organization);

        validateAdminPermission(member.getId(), organization, OrganizationRole.ADMIN);

        validateOrganizationGroupName(organizationGroup.getName(), organization);

        organizationGroup.updateOrganization(organization);

        return organizationGroupRepository.save(organizationGroup);

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

            return s3Uploader.upload("organization/", file);
        }
        return null;
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

    private void validateOrganizationGroupName(final String name, final Organization organization) {
        if (organizationGroupRepository.findByNameAndOrganization(name, organization).isPresent()) {
            throw new OrganizationGroupNameDuplicatedException();
        }
    }
}