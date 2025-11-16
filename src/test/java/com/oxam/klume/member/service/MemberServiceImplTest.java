package com.oxam.klume.member.service;

import com.oxam.klume.auth.service.MailService;
import com.oxam.klume.common.error.exception.*;
import com.oxam.klume.common.service.ProfileImageServiceImpl;
import com.oxam.klume.member.dto.LoginRequestDTO;
import com.oxam.klume.member.dto.LoginResponseDTO;
import com.oxam.klume.member.dto.SignupRequestDTO;
import com.oxam.klume.member.dto.SignupResponseDTO;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.entity.SystemRole;
import com.oxam.klume.member.entity.enums.Provider;
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import com.oxam.klume.member.repository.SystemRoleRepository;
import com.oxam.klume.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("MemberService 단위 테스트")
class MemberServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImplTest.class);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ProfileImageServiceImpl profileImageService;

    @Mock
    private SystemRoleRepository systemRoleRepository;

    @Mock
    private MemberSystemRoleRepository memberSystemRoleRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private SignupRequestDTO signupRequest;
    private LoginRequestDTO loginRequest;
    private Member testMember;
    private SystemRole memberRole;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 회원가입 요청 DTO
        signupRequest = new SignupRequestDTO();
        java.lang.reflect.Field emailField;
        java.lang.reflect.Field passwordField;
        try {
            emailField = SignupRequestDTO.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(signupRequest, "test@example.com");

            passwordField = SignupRequestDTO.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(signupRequest, "password123!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Given: 테스트용 로그인 요청 DTO
        loginRequest = new LoginRequestDTO("test@example.com", "password123!");

        // Given: 테스트용 회원 엔티티
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

        // Given: 테스트용 시스템 역할
        memberRole = new SystemRole();
        java.lang.reflect.Field idField;
        java.lang.reflect.Field nameField;
        java.lang.reflect.Field descriptionField;
        try {
            idField = SystemRole.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(memberRole, 1);

            nameField = SystemRole.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(memberRole, Role.MEMBER);

            descriptionField = SystemRole.class.getDeclaredField("description");
            descriptionField.setAccessible(true);
            descriptionField.set(memberRole, "일반 회원");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("정상적인 회원가입 - 성공")
        void signup_Success() {
            // Given
            given(mailService.isEmailVerified(signupRequest.getEmail())).willReturn(true);
            given(memberRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
            given(passwordEncoder.encode(signupRequest.getPassword())).willReturn("encodedPassword");
            given(profileImageService.getRandomProfileImageUrl()).willReturn("https://example.com/profile.jpg");
            given(memberRepository.save(any(Member.class))).willReturn(testMember);
            given(systemRoleRepository.findByName(Role.MEMBER)).willReturn(Optional.of(memberRole));
            given(memberSystemRoleRepository.save(any(MemberSystemRole.class))).willReturn(new MemberSystemRole());

            // When
            SignupResponseDTO response = memberService.signup(signupRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1);
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getCreatedAt()).isNotNull();

            // Verify
            then(mailService).should().isEmailVerified(signupRequest.getEmail());
            then(memberRepository).should().existsByEmail(signupRequest.getEmail());
            then(passwordEncoder).should().encode(signupRequest.getPassword());
            then(profileImageService).should().getRandomProfileImageUrl();
            then(memberRepository).should().save(any(Member.class));
            then(systemRoleRepository).should().findByName(Role.MEMBER);
            then(memberSystemRoleRepository).should().save(any(MemberSystemRole.class));
            then(mailService).should().clearVerification(signupRequest.getEmail());
        }

        @Test
        @DisplayName("이메일 미인증 상태로 회원가입 시도 - 실패")
        void signup_EmailNotVerified_ThrowsException() {
            // Given
            given(mailService.isEmailVerified(signupRequest.getEmail())).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> memberService.signup(signupRequest))
                    .isInstanceOf(EmailNotVerifiedException.class);

            // Verify
            then(mailService).should().isEmailVerified(signupRequest.getEmail());
            then(memberRepository).should(never()).existsByEmail(anyString());
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입 시도 - 실패")
        void signup_EmailAlreadyExists_ThrowsException() {
            // Given
            given(mailService.isEmailVerified(signupRequest.getEmail())).willReturn(true);
            given(memberRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> memberService.signup(signupRequest))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            // Verify
            then(mailService).should().isEmailVerified(signupRequest.getEmail());
            then(memberRepository).should().existsByEmail(signupRequest.getEmail());
            then(passwordEncoder).should(never()).encode(anyString());
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("MEMBER 역할이 존재하지 않을 경우 - 실패")
        void signup_MemberRoleNotFound_ThrowsException() {
            // Given
            given(mailService.isEmailVerified(signupRequest.getEmail())).willReturn(true);
            given(memberRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
            given(passwordEncoder.encode(signupRequest.getPassword())).willReturn("encodedPassword");
            given(profileImageService.getRandomProfileImageUrl()).willReturn("https://example.com/profile.jpg");
            given(memberRepository.save(any(Member.class))).willReturn(testMember);
            given(systemRoleRepository.findByName(Role.MEMBER)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberService.signup(signupRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("MEMBER 역할이 존재하지 않습니다.");

            // Verify
            then(systemRoleRepository).should().findByName(Role.MEMBER);
            then(memberSystemRoleRepository).should(never()).save(any(MemberSystemRole.class));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("정상적인 로그인 - 성공")
        void login_Success() {
            // Given
            given(memberRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testMember));
            given(passwordEncoder.matches(loginRequest.getPassword(), testMember.getPassword())).willReturn(true);
            given(jwtUtil.createAccessToken(testMember.getEmail())).willReturn("test-jwt-token");

            // When
            LoginResponseDTO response = memberService.login(loginRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("test-jwt-token");
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getProfileImage()).isEqualTo("https://example.com/profile.jpg");
            assertThat(response.getMessage()).isEqualTo("로그인 성공");

            // Verify
            then(memberRepository).should().findByEmail(loginRequest.getEmail());
            then(passwordEncoder).should().matches(loginRequest.getPassword(), testMember.getPassword());
            then(jwtUtil).should().createAccessToken(testMember.getEmail());
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시도 - 실패")
        void login_MemberNotFound_ThrowsException() {
            // Given
            given(memberRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class);

            // Verify
            then(memberRepository).should().findByEmail(loginRequest.getEmail());
            then(passwordEncoder).should(never()).matches(anyString(), anyString());
            then(jwtUtil).should(never()).createAccessToken(anyString());
        }

        @Test
        @DisplayName("소셜 로그인 회원이 비밀번호 로그인 시도 - 실패")
        void login_SocialLoginMember_ThrowsException() {
            // Given
            Member googleMember = Member.builder()
                    .id(1)
                    .email("test@example.com")
                    .password(null)
                    .provider(Provider.GOOGLE)
                    .providerId("google-123456")
                    .imageUrl("https://example.com/profile.jpg")
                    .createdAt("2025-11-16 10:00:00")
                    .isDeleted(false)
                    .isNotificationEnabled(true)
                    .build();

            given(memberRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(googleMember));

            // When & Then
            assertThatThrownBy(() -> memberService.login(loginRequest))
                    .isInstanceOf(SocialLoginRequiredException.class);

            // Verify
            then(memberRepository).should().findByEmail(loginRequest.getEmail());
            then(passwordEncoder).should(never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시도 - 실패")
        void login_InvalidPassword_ThrowsException() {
            // Given
            given(memberRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(testMember));
            given(passwordEncoder.matches(loginRequest.getPassword(), testMember.getPassword())).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> memberService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class);

            // Verify
            then(memberRepository).should().findByEmail(loginRequest.getEmail());
            then(passwordEncoder).should().matches(loginRequest.getPassword(), testMember.getPassword());
            then(jwtUtil).should(never()).createAccessToken(anyString());
        }

        @Test
        @DisplayName("탈퇴한 회원이 로그인 시도 - 실패")
        void login_DeletedMember_ThrowsException() {
            // Given
            Member deletedMember = Member.builder()
                    .id(1)
                    .email("test@example.com")
                    .password("encodedPassword")
                    .provider(null)
                    .imageUrl("https://example.com/profile.jpg")
                    .createdAt("2025-11-16 10:00:00")
                    .isDeleted(true)  // 탈퇴한 회원
                    .isNotificationEnabled(true)
                    .build();

            given(memberRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(deletedMember));
            given(passwordEncoder.matches(loginRequest.getPassword(), deletedMember.getPassword())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> memberService.login(loginRequest))
                    .isInstanceOf(MemberDeletedException.class);

            // Verify
            then(memberRepository).should().findByEmail(loginRequest.getEmail());
            then(passwordEncoder).should().matches(loginRequest.getPassword(), deletedMember.getPassword());
            then(jwtUtil).should(never()).createAccessToken(anyString());
        }
    }

    @Nested
    @DisplayName("이메일로 회원 조회 테스트")
    class FindMemberByEmailTest {

        @Test
        @DisplayName("이메일로 회원 조회 - 성공")
        void findMemberByEmail_Success() {
            // Given
            given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(testMember));

            // When
            Member foundMember = memberService.findMemberByEmail("test@example.com");

            // Then
            assertThat(foundMember).isNotNull();
            assertThat(foundMember.getId()).isEqualTo(1);
            assertThat(foundMember.getEmail()).isEqualTo("test@example.com");

            // Verify
            then(memberRepository).should().findByEmail("test@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 회원 조회 - 실패")
        void findMemberByEmail_NotFound_ThrowsException() {
            // Given
            given(memberRepository.findByEmail("notfound@example.com")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberService.findMemberByEmail("notfound@example.com"))
                    .isInstanceOf(MemberNotFoundException.class);

            // Verify
            then(memberRepository).should().findByEmail("notfound@example.com");
        }
    }
}
