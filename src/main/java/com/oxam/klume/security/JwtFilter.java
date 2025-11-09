package com.oxam.klume.security;

import com.oxam.klume.member.entity.Member;
import com.oxam.klume.member.entity.MemberSystemRole;
import com.oxam.klume.member.repository.MemberRepository;
import com.oxam.klume.member.repository.MemberSystemRoleRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/* 설명. HTTP 요청에서 JWT를 추출하고 인증 처리하는 필터 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final MemberSystemRoleRepository memberSystemRoleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Authorization 헤더에서 JWT 토큰 추출
            String token = getTokenFromRequest(request);

            // 2. 토큰이 있고 유효하면 인증 처리
            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);

                // 3. DB에서 회원 조회
                Optional<Member> memberOptional = memberRepository.findByEmail(email);

                if (memberOptional.isPresent()) {
                    Member member = memberOptional.get();

                    // 4. DB에서 회원의 역할 조회
                    List<MemberSystemRole> memberRoles = memberSystemRoleRepository.findByMemberId(member.getId());

                    // 5. 역할을 권한으로 변환 (MEMBER -> ROLE_MEMBER, ADMIN -> ROLE_ADMIN)
                    List<GrantedAuthority> authorities = memberRoles.stream()
                            .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.getSystemRole().getName().name()))
                            .collect(Collectors.toList());

                    // 6. SecurityContext에 인증 정보 저장 (email을 principal로 사용)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공 - email: {}, authorities: {}", email, authorities);
                } else {
                    log.warn("JWT 토큰의 이메일에 해당하는 회원이 존재하지 않음: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
        }

        // 4. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /* 설명. HTTP 요청에서 JWT 토큰 추출 */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
