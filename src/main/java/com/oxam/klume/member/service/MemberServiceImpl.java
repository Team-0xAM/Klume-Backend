package com.oxam.klume.member.service;

import com.oxam.klume.member.dto.SignupRequest;
import com.oxam.klume.member.dto.SignupResponse;
import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.repository.MemberRepository;
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
}
