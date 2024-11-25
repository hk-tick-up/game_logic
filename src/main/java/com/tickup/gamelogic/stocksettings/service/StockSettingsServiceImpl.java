package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.stocksettings.domain.CompanyInfos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockSettingsServiceImpl implements StockSettingsService {

    @Override
    public void generateScenarioForGameRoom(GameRooms gameRoom) {
        // 기업 4개 랜덤 선택
        List<CompanyInfos> selectedCompanies;

    }
}
