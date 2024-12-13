package com.tickup.gamelogic.ml.domain;

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
@Table(name = "Corporation_tb")
public class MLCorporation {
    @Id
    @Column(name = "corp_ticker")
    private String corpTicker;

    @Column(nullable = false, name = "corp_name")
    private String corpName;

    @Column(nullable = false, name = "corp_ind")
    private String corpIndustry;

    @Column(nullable = false, name = "corp_info")
    private String corpInfo;
}
