package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;

public interface StockSettingsService {
    void generateScenarioForGameRoom(GameRooms gameRoom);
    // 기업 정보 가져오기 (게임 방 생성 시 4개 기업 정보 초기화)
    // (기업별) 이벤트 정보 가져오기
    // (기업별) 주가 정보 가져오기
}
