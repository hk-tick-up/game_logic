package com.tickup.gamelogic.config.websocket;

import com.tickup.gamelogic.gamerooms.service.GameRoomService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final GameRoomService gameRoomService;

    public WebSocketEventListener(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");
        if (destination != null && destination.startsWith("/topic/game-room/")) {
            Long gameRoomId = Long.parseLong(destination.split("/")[3]);
            gameRoomService.sendInitialGameState(gameRoomId);
        }
    }
}

