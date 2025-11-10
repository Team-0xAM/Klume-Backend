package com.oxam.klume.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;

/* 설명. WebSocket STOMP 메시지에서 JWT를 추출하고 인증 처리하는 인터셉터
 *       Principal에 이메일을 설정하여 ChatController에서 사용할 수 있게 함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // WebSocket 연결 시 JWT 토큰 검증
            String token = getTokenFromHeader(accessor);

            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                
                // Principal에 이메일 설정 (ChatController에서 principal.getName()으로 사용 가능)
                Principal principal = new UsernamePasswordAuthenticationToken(email, null);
                accessor.setUser(principal);
                
                log.info("WebSocket JWT 인증 성공 - email: {}", email);  //임시 로그 (인증 되는지)
            } else {
                log.warn("WebSocket JWT 인증 실패 - token: {}", token != null ? "invalid" : "null");
            }
        }

        return message;
    }

    /* 설명. STOMP 헤더에서 JWT 토큰 추출 */
    private String getTokenFromHeader(StompHeaderAccessor accessor) {
        // Authorization 헤더에서 토큰 추출
       // jWT 전송 표준 형식(약속된 문자열)
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}

