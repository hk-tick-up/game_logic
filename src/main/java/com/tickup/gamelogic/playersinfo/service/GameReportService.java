package com.tickup.gamelogic.playersinfo.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.playersinfo.request.GameReportRequest;

import java.util.List;

public interface GameReportService {
    List<GameReportRequest> createGameReport(GameRooms gameRoom, String userId);
}
