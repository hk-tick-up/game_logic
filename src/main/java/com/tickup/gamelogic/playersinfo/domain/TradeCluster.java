package com.tickup.gamelogic.playersinfo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeCluster {
    @Id
    @Column(nullable = false, unique = true)
    private Integer tradeClusterId; // 클러스터 번호 (PK)

    @Column(name = "name", nullable = false)
    private String name; // 클러스터 이름 (예: 공격적인 매수형 투자자)

    @Column(name = "tagline", nullable = false)
    private String tagline; // 캐치프레이즈

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description; // 클러스터 특징 (매수 집중, 높은 자산 운용 등)

    @Column(name = "traits", columnDefinition = "TEXT", nullable = false)
    private String traits; // 성격 요약 (과감히 앞으로!, 현금 중시 등)

    @Column(name = "remark", columnDefinition = "TEXT", nullable = false)
    private String remark; // 한마디 (공격적인 투자로 미래를 바꾼다!)

}
