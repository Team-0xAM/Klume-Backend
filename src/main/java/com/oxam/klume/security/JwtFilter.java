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
            String token = getTokenFromRequest(request);

            if (token != null) {
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.getEmailFromToken(token);
                    Optional<Member> memberOptional = memberRepository.findByEmail(email);

                    if (memberOptional.isPresent()) {
                        Member member = memberOptional.get();
                        List<MemberSystemRole> memberRoles = memberSystemRoleRepository.findByMemberId(member.getId());

                        List<GrantedAuthority> authorities = memberRoles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getSystemRole().getName().name()))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(email, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.info("JWT 이메일에 해당하는 회원이 없음: {}", email);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: user not found");
                        return;
                    }
                } else {
                    log.info("JWT 토큰이 유효하지 않음");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 중 예외 발생: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT authentication error");
            return;
        }

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
