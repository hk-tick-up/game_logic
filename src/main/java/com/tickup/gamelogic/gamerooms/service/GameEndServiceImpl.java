package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.gamerooms.response.GameEndDataResponse;
import com.tickup.gamelogic.gamerooms.response.GameEndResponse;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.playersinfo.repository.CurrentPlayersInfoRepository;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import com.tickup.gamelogic.stocksettings.repository.StockDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameEndServiceImpl implements GameEndService {
    private final StockDataRepository stockDataRepository;
    private final CurrentPlayersInfoRepository playersInfoRepository;

    @Override
    public GameEndDataResponse sendGameEndData(Long gameRoomId, String userId) {
        CurrentPlayersInfo currentPlayersInfo = playersInfoRepository
                .findByGameRoomIdAndUserId(gameRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("sendGameEndData: Player not found"));

        List<StockData> lastTurnStockInfos =
                stockDataRepository.findAllByGameRoomAndTurn(gameRoomId, 5);

        return GameEndDataResponse.from(currentPlayersInfo, lastTurnStockInfos);

    }
}
