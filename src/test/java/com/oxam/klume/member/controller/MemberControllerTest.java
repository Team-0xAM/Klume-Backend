package com.oxam.klume.member.controller;

import com.oxam.klume.common.error.exception.MemberNotFoundException;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.service.MemberService;
import com.oxam.klume.organization.dto.OrganizationResponseDTO;
import com.oxam.klume.organization.entity.Organization;
import com.oxam.klume.organization.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("MemberController 단위 테스트")
class MemberControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MemberControllerTest.class);

    @Mock
    private MemberService memberService;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private MemberController memberController;

    private Member testMember;
    private List<Organization> testOrganizations;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 회원
        testMember = Member.builder()
                .id(1)
                .email("test@example.com")
                .password("encodedPassword")
                .provider(null)
                .imageUrl("https://example.com/profile.jpg")
                .createdAt("2025-11-16 10:00:00")
                .isDeleted(false)
                .isNotificationEnabled(true)
                .build();

        // Given: 테스트용 조직 목록
        Organization org1 = createOrganization(1, "한화 시스템 BEYOND SW 캠프", "개발자 부트캠프", "https://example.com/org1.jpg");
        Organization org2 = createOrganization(2, "테스트 조직", "테스트 설명", "https://example.com/org2.jpg");
        testOrganizations = Arrays.asList(org1, org2);

        // Given: Mock Authentication
        authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn("test@example.com");
    }

    private Organization createOrganization(int id, String name, String description, String imageUrl) {
        Organization organization = new Organization(name, description, imageUrl);

        // Reflection을 사용하여 ID 설정
        try {
            java.lang.reflect.Field idField = Organization.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(organization, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return organization;
    }

    @Test
    @DisplayName("속한 조직 목록 조회 - 성공")
    void getMyOrganizations_Success() {
        // Given
        given(memberService.findMemberByEmail("test@example.com")).willReturn(testMember);
        given(organizationService.findOrganizationByMember(testMember)).willReturn(testOrganizations);

        // When
        ResponseEntity<List<OrganizationResponseDTO>> response = memberController.getMyOrganizations(authentication);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getOrganizationId()).isEqualTo(1);
        assertThat(response.getBody().get(1).getOrganizationId()).isEqualTo(2);

        // Verify
        then(memberService).should().findMemberByEmail("test@example.com");
        then(organizationService).should().findOrganizationByMember(testMember);
    }

    @Test
    @DisplayName("속한 조직이 없는 경우 - 빈 배열 반환")
    void getMyOrganizations_EmptyList() {
        // Given
        given(memberService.findMemberByEmail("test@example.com")).willReturn(testMember);
        given(organizationService.findOrganizationByMember(testMember)).willReturn(Collections.emptyList());

        // When
        ResponseEntity<List<OrganizationResponseDTO>> response = memberController.getMyOrganizations(authentication);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        // Verify
        then(memberService).should().findMemberByEmail("test@example.com");
        then(organizationService).should().findOrganizationByMember(testMember);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 조직 조회 시도 - 실패")
    void getMyOrganizations_MemberNotFound_ThrowsException() {
        // Given
        given(authentication.getPrincipal()).willReturn("notfound@example.com");
        given(memberService.findMemberByEmail("notfound@example.com"))
                .willThrow(new MemberNotFoundException());

        // When & Then
        assertThatThrownBy(() -> memberController.getMyOrganizations(authentication))
                .isInstanceOf(MemberNotFoundException.class);

        // Verify
        then(memberService).should().findMemberByEmail("notfound@example.com");
    }
}
