package com.oxam.klume.auth.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/* 설명. OAuth2 로그인 시 사용자 정보를 담는 클래스
 *      Spring Security가 인증된 사용자로 인식하는 객체
* */
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final String name;
    private final String imageUrl;
    private final String providerId;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 현재는 권한 관리가 없으므로 빈 컬렉션 반환
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return name;
    }
}
