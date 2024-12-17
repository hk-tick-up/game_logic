package com.tickup.gamelogic.playersinfo.controller;

import com.tickup.gamelogic.playersinfo.service.GameHistoryQueryService;
import com.tickup.gamelogic.stocksettings.response.ClusterDetailResponse;
import com.tickup.gamelogic.stocksettings.response.GameHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamehistory")
@RequiredArgsConstructor
public class GameHistoryController {
    private final GameHistoryQueryService gameHistoryQueryService;

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<GameHistoryResponse>> getUserGameHistory(@PathVariable String userId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameHistoryQueryService.getUserGameHistory(userId));
    }

    @GetMapping("/{userId}/history/{gameRoomId}/cluster")
    public ResponseEntity<ClusterDetailResponse> getClusterDetails(
            @PathVariable String userId, @PathVariable Long gameRoomId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameHistoryQueryService.getClusterDetails(userId, gameRoomId));
    }
}
