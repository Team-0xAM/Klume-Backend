package com.oxam.klume.config;

import com.oxam.klume.security.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")       // http://localhost:8080/ws
                .setAllowedOriginPatterns("*")   // CORS 설정 전부 허용 (임시)
                .withSockJS();                   // SockJS 사용

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");    // 내장 Brocker 사용
        registry.setApplicationDestinationPrefixes("/app");       // 클라이언트 -> 서버 endpoint

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // WebSocket 메시지 수신 시 JWT 인증 인터셉터 등록
        //registration.interceptors(jwtChannelInterceptor);
    }
}
