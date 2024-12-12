package com.tickup.gamelogic.gamerooms.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GameRoomsRestController {

//    private final GameRoomServiceImpl gameRoomService;
//
//    @MessageMapping("/game-room/{gameRoomId}")
//    @SendTo("/topic/game-room/{gameRoomId}/game-process")
////    @PostMapping("/{gameRoomId}/update-turn")
////    public ResponseEntity<?> updateTurn(@PathVariable("gameRoomId") Long gameRoomId) {
////        TurnUpdateResponse reponse =
////        gameRoomService.updateTurn(gameRoomId);
////
////        return ResponseEntity.ok().build();
////    }
//    public TurnUpdateResponse handleGameTurn(Long gameRoomId) {
//        gameRoomService.updateTurn(gameRoomId);
//        TurnUpdateResponse response = new TurnUpdateResponse();
//        log.info("Turn updated for gameRoomId: {}", gameRoomId);
//        return response;
//    }

}
