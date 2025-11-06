package com.oxam.klume.organization.service;

import com.oxam.klume.file.FileValidator;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.exception.MemberNotFoundException;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.organization.dto.OrganizationRequestDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.entity.OrganizationMember;
import com.oxam.klume.organization.entity.enums.OrganizationRole;
import com.oxam.klume.organization.repository.OrganizationMemberRepository;
import com.oxam.klume.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    private final FileValidator fileValidator;

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

    private String uploadImage(final MultipartFile file) {
        if (file != null) {
            fileValidator.validateImage(file);
            // TODO s3 업로드 후 이미지 url 반환
            return "https://~";
        }
        return  null;
    }

    private Member findMemberById(final int memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}