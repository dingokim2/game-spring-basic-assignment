package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class DeckCount {
    private Long gameId;
    private long count;

    public DeckCount(Long gameId, long count) {
        this.gameId = gameId;
        this.count = count;
    }
}
