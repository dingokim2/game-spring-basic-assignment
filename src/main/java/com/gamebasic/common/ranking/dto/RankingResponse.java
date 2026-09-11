package com.gamebasic.common.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private String season;
    private int totalRecords;
    private int excludedCount;
    private List<Entry> entries;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry{
        private int rank;
        private String playerName;
        private int clearTimeSeconds;
        private int remainingHp;
        private int bossTurns;
        private int deckSize;
    }
}
