package com.oxam.klume.member.service;

import com.oxam.klume.auth.service.MailService;
import com.oxam.klume.member.dto.LoginRequest;
import com.oxam.klume.member.dto.LoginResponse;
import com.oxam.klume.member.dto.SignupRequest;
import com.oxam.klume.member.dto.SignupResponse;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
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

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 1. 이메일 인증 확인
        if (!mailService.isEmailVerified(request.getEmail())) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다. 먼저 이메일 인증을 진행해주세요.");
        }

        // 2. 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 회원 생성
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .provider(null)  // 로컬 회원은 null
                .createdAt(now)
                .isDeleted(false)
                .isNotificationEnabled(true)
                .build();

        Member savedMember = memberRepository.save(member);

        // 5. 인증 상태 삭제 (회원가입 완료 후)
        mailService.clearVerification(request.getEmail());

        // 6. 응답 생성
        return SignupResponse.builder()
                .id(savedMember.getId())
                .email(savedMember.getEmail())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 1. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 로컬 회원인지 확인 (구글 회원은 비밀번호 로그인 불가)
        if (member.getProvider() != null) {
            throw new IllegalArgumentException("소셜 로그인 회원은 해당 소셜 계정으로 로그인해주세요.");
        }

        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 4. 탈퇴한 회원인지 확인
        if (member.isDeleted()) {
            throw new IllegalArgumentException("탈퇴한 회원입니다.");
        }

        // 5. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(member.getEmail());

        // 6. 응답 생성
        return LoginResponse.builder()
                .accessToken(accessToken)
                .email(member.getEmail())
                .message("로그인 성공")
                .build();
    }
}
