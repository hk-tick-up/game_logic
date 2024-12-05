package com.tickup.gamelogic.config.websocket;

import com.tickup.gamelogic.gamerooms.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final GameRoomService gameRoomService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {

    }
}

