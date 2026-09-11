package com.gamebasic.common.ranking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.smartcardio.Card;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingSource {
    private Meta meta;
        private List<Record> records;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private Season season;
        //private String generatedAt;
        //private int schemaVersion;
        // private int totalRecord;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Season {
        private String id;
        //private String name;
        //private String startsAt;
        //private String endsAt;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record {
        private Long id;
        // private String submittedAt;
        // private Client client;
        private Player player;
        private Run run;
        private BossFight bossFight;
        private Deck deck;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Client {
        private String version;
        private String platform;
        private String locale;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player {
        private String id; // <--------------------- 동일인인지 확인할 때 필요
        private String name; // <---------------------
        // private String region;
        // private List<String> tags;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Run {
        // private String seed;
        private String status; // <--------------------- CLEARED 인지 확인하여 필터링
        private int clearedFloor; // <--------------------- 10 인지 확인하여 필터링
        private int durationSeconds; // <---------------------
        private int finalHp; // <---------------------
        //private List<Floor>> floors;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BossFight {
        private List<Phase> phases; // <--------------------- 원소 3개가 모두 정상인지 확인. (null일 수 있기 때문에 배열이 아닌 리스트로 선언함)
        private String finishingCard; // <--------------------- Deck에 존재하는 Card의 cardType인지 확인
        private int totalTurns; // <---------------------
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phase {
        private BossPhase phase; // <--------------------- THRONE, UNBOUND, ECLIPSE 인지 확인 (순서 중요)
        private int turns; // <--------------------- 1 이상인지 확인, 합이 totalTurns와 같은지도 확인
        private int damageTaken;
    }

    public enum BossPhase {
        THRONE, UNBOUND, ECLIPSE
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Deck {
        private int size; // <---------------------
        private List<Card> cards; // <---------------------size값과 개수가 같은지 확인
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String cardType; // <--------------------- 모두 유효한 카드타입인지 확인
        private int acquiredFloor; // <--------------------- 0이상 9이하인지 확인
    }
}
