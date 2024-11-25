package com.tickup.gamelogic.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

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
        //client가 구독할 수 있는 주제 경로
        config.enableSimpleBroker("/topic");

        // client가 메시지를 보낼 때 사용할 경로
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint 정의
        registry.addEndpoint("ws").setAllowedOrigins("*").withSockJS();
    }


}
