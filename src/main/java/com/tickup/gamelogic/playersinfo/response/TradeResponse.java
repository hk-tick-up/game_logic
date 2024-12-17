package com.tickup.gamelogic.playersinfo.response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.response.InitGameProcessResponse;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;

import java.time.LocalDateTime;
import java.util.List;

public record TradeResponse(
        boolean success,
        String message,
        int balance,
        int netAssets,
        double returnRate,
        List<InvestmentDetail> investments
) {

    // 성공 시 메시지
    public static TradeResponse from (CurrentPlayersInfo player,
                                      List<InvestmentDetail> investments,
                                      boolean success,
                                      String message
    ) {
        return new TradeResponse(
                success,
                message,
                player.getBalance(),
                player.getNetAssets(),
                player.getReturnRate(),
                investments
        );
    }

    // 실패 시 메시지
    public static TradeResponse from(boolean success, String message) {
        return new TradeResponse(
                success,
                message,
                0, // 실패 시 balance 없음
                0, // 실패 시 netAssets 없음
                0, // 실패 시 returnRate 없음
                List.of()  // 실패 시 investments 없음
        );
    }

}
