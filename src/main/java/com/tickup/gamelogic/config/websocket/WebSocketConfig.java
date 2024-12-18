package com.tickup.gamelogic.config.websocket;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/*
 * Class name: WebSocketConfig
 * Summary: WebSocket config 설정
 * Date: 2024.11.21
 * Write by: 양예현
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 수 있는 경로
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지 보낼 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket 연결 엔드포인트
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://192.168.1.6:3000",
                        "http://192.168.45.94:3000"
                ) // 허용된 도메인
                .setHandshakeHandler(new DefaultHandshakeHandler()) // 기본 핸드셰이크 처리
                .withSockJS() // SockJS 지원 활성화
                ; // 쿠키 기반 세션 필요
    }
}