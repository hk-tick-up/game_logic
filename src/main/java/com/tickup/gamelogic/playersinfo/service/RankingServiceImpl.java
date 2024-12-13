package com.tickup.gamelogic.playersinfo.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.playersinfo.repository.CurrentPlayersInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingServiceImpl implements RankingService {

    private final CurrentPlayersInfoRepository currentPlayersInfoRepository;

    @Override
    public void updateRankings(GameRooms gameRoom) {
        // 해당 게임룸의 모든 플레이어 정보를 수익률 기준으로 조회
        List<CurrentPlayersInfo> sortedPlayers
                = currentPlayersInfoRepository.findAllByGameRoomsOrderByReturnRateDesc(gameRoom);

        // 순위 업데이트
        for (int i = 0 ; i < sortedPlayers.size() ; i++) {
            CurrentPlayersInfo player = sortedPlayers.get(i);
            player.updateRank(i + 1);
        }

        // 저장
        currentPlayersInfoRepository.saveAll(sortedPlayers);
    }

}
