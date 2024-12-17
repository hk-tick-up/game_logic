package com.tickup.gamelogic.playersinfo.controller;

import com.tickup.gamelogic.playersinfo.request.TradeRequest;
import com.tickup.gamelogic.playersinfo.response.MyInvestmentResponse;
import com.tickup.gamelogic.playersinfo.response.TradeResponse;
import com.tickup.gamelogic.playersinfo.service.TradeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamelogic/{gameRoomId}/trade")
public class TradeController {

    private final TradeServiceImpl tradeService;

    @PostMapping("")
    public ResponseEntity<TradeResponse> tradeShares(
            @PathVariable Long gameRoomId,
            @RequestBody TradeRequest request
    ) {

        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = authentication.getName();

        TradeResponse response = tradeService.processTrade(
                gameRoomId,
                request.userId(),
                request.ticker(),
                request.shares(),
                request.tradeType()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/investments")
    public ResponseEntity<MyInvestmentResponse> getInvestments(
            @PathVariable Long gameRoomId,
            @RequestParam String userId
    ) {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = authentication.getName();

        MyInvestmentResponse response = tradeService.getInvestments(gameRoomId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }

}
