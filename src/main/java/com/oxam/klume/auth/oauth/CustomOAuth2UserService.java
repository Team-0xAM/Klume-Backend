package com.oxam.klume.auth.oauth;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.entity.SystemRole;
import com.oxam.klume.member.entity.enums.Provider;
import com.oxam.klume.member.entity.enums.Role;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import com.oxam.klume.member.repository.SystemRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/* 설명. OAuth2 로그인 시 사용자 정보를 처리하는 서비스
 *       구글에서 받은 사용자 정보로 회원가입/로그인 처리
* */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글에서 사용자 정보 가져오기
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. 구글 사용자 정보 추출
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String providerId = (String) attributes.get("sub");  // 구글 고유 ID

        log.info("구글 로그인 시도 - email: {}, name: {}", email, name);

        // 3. DB에서 회원 조회 또는 생성
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> createMember(email, name, picture, providerId));

        log.info("회원 처리 완료 - memberId: {}, email: {}", member.getId(), member.getEmail());

        // 4. CustomOAuth2User 객체 반환
        return new CustomOAuth2User(
                member.getEmail(),
                name,
                picture,
                providerId,
                attributes
        );
    }

    /* 설명. 새로운 구글 회원 생성 */
    private Member createMember(String email, String name, String picture, String providerId) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Member member = Member.builder()
                .email(email)
                .password(null)  // 구글 로그인은 비밀번호 없음
                .provider(Provider.GOOGLE)
                .providerId(providerId)
                .imageUrl(picture)
                .createdAt(now)
                .isDeleted(false)
                .isNotificationEnabled(true)
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("새로운 구글 회원 생성 - memberId: {}, email: {}", savedMember.getId(), savedMember.getEmail());

        // MEMBER 역할 부여
        SystemRole memberRole = systemRoleRepository.findByName(Role.MEMBER)
                .orElseThrow(() -> new IllegalStateException("MEMBER 역할이 존재하지 않습니다."));

        MemberSystemRole memberSystemRole = MemberSystemRole.builder()
                .member(savedMember)
                .systemRole(memberRole)
                .build();

        memberSystemRoleRepository.save(memberSystemRole);
        log.info("구글 회원에게 MEMBER 역할 부여 완료");

        return savedMember;
    }
}
