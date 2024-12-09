package com.tickup.gamelogic.stocksettings.response;

import java.util.Map;

public record TurnStockDataResponse(
        Long GameRoomId,
        int currentTurn,
        Map<String, CompanyTurnResponse> companyTurnResponse
) {
    public static TurnStockDataResponse from(
        Long GameRoomId,
        int currentTurn,
        Map<String, CompanyTurnResponse> companyTurnResponse
    ) {
        return new TurnStockDataResponse(
                GameRoomId,
                currentTurn,
                companyTurnResponse
        );
    }
}
