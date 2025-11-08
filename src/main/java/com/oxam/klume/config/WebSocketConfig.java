package com.oxam.klume.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
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
}
