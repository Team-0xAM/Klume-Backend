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
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import com.oxam.klume.member.repository.SystemRoleRepository;
import com.oxam.klume.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final ProfileImageServiceImpl profileImageServiceImpl;
    private final SystemRoleRepository systemRoleRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    @Override
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO request) {

        // 1. 이메일 인증 확인
        if (!mailService.isEmailVerified(request.getEmail())) {
            throw new EmailNotVerifiedException();
        }

        // 2. 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 랜덤 프로필 이미지 URL 생성
        String profileImageUrl = profileImageServiceImpl.getRandomProfileImageUrl();

        // 5. 회원 생성
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .provider(null)  // 로컬 회원은 null
                .imageUrl(profileImageUrl)  // 랜덤 프로필 이미지
                .createdAt(now)
                .isDeleted(false)
                .isNotificationEnabled(true)
                .build();

        Member savedMember = memberRepository.save(member);

        // 6. MEMBER 역할 부여
        SystemRole memberRole = systemRoleRepository.findByName(Role.MEMBER)
                .orElseThrow(() -> new IllegalStateException("MEMBER 역할이 존재하지 않습니다."));

        MemberSystemRole memberSystemRole = MemberSystemRole.builder()
                .member(savedMember)
                .systemRole(memberRole)
                .build();

        memberSystemRoleRepository.save(memberSystemRole);

        // 7. 인증 상태 삭제 (회원가입 완료 후)
        mailService.clearVerification(request.getEmail());

        // 8. 응답 생성
        return SignupResponseDTO.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. 로컬 회원인지 확인 (구글 회원은 비밀번호 로그인 불가)
        if (member.getProvider() != null) {
            throw new SocialLoginRequiredException();
        }

        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 4. 탈퇴한 회원인지 확인
        if (member.isDeleted()) {
            throw new MemberDeletedException();
        }

        // 5. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(member.getEmail());

        // 6. 응답 생성
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .profileImage(member.getImageUrl())
                .message("로그인 성공")
                .build();
    }

    @Override
    public Member findMemberByEmail(final String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}