package com.gamebasic.common.ranking.service;

import com.gamebasic.common.ranking.client.RankingClient;
import com.gamebasic.common.ranking.dto.RankingResponse;
import com.gamebasic.common.ranking.dto.RankingSource;
import com.gamebasic.runcard.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingClient rankingClient;

    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();

        String season = source.getMeta().getSeason().getId();

        int totalRecords = 0;
        int excludedCount = 0;

        Map<String,RecordSummary> map = new HashMap<>();

        for(RankingSource.Record record : source.getRecords()){

            totalRecords++;

            // 순위 대상 확인
            if(!record.getRun().getStatus().equals("CLEARED")){
                continue;
            }
            if(record.getRun().getClearedFloor() != 10){
                continue;
            }

            // 정상 기록 확인
            if(!isValidRecord(record)) {
                excludedCount++;
                continue;
            }

            RecordSummary current = new RecordSummary(record);

            // 이미 기록이 있는 플레이어의 경우 이전 기록과 비교
            if(map.containsKey(current.getPlayerId())){
                RecordSummary previous = map.get(current.getPlayerId());
                // 기존 기록이 현재 기록보다 좋은 경우 continue
                if(RecordSummary.getComparator().compare(previous, current) < 0) {
                    continue;
                }
            }

            // 현재 기록을 추가 (기존 기록이 있으면 덮어씌움)
            map.put(current.getPlayerId(), current);
        }

        // Map을 List로 변환
        int entryCount = map.size();
        List<RecordSummary> summaries = new ArrayList<>();
        for(RecordSummary summary : map.values()){
            summaries.add(summary);
        }

        // List 정렬
        summaries.sort(RecordSummary.getComparator());

        RankingResponse.Entry[] entrieArray = new RankingResponse.Entry[entryCount];
        for(int i = 0; i < entryCount; i++){
            int rank = i + 1;
            RecordSummary summary = summaries.get(i);
            entrieArray[i] = new RankingResponse.Entry(
                    rank,
                    summary.getPlayerName(),
                    summary.getClearTimeSeconds(),
                    summary.getRemainingHp(),
                    summary.getBossTurns(),
                    summary.getDeckSize()
            );;
        }

        return new RankingResponse(
                season,
                totalRecords,
                excludedCount,
                Arrays.asList(entrieArray)
        );
    }

    private boolean isValidRecord(RankingSource.Record record){
        // 1. 클리어 시간
        if(record.getRun().getDurationSeconds() < 300) return false;

        // 2. 남은 Hp
        if(record.getRun().getFinalHp() < 1 || record.getRun().getFinalHp() > 99) return false;

        // 3. 덱 크기
        if(record.getDeck().getSize() < 9 || record.getDeck().getSize() > 20) return false;
        if(record.getDeck().getSize() != record.getDeck().getCards().size()) return false;

        for(RankingSource.Card card : record.getDeck().getCards()){
            // 4. 카드 타입
            if(!Arrays.stream(CardType.values())
                    .anyMatch(cardType -> cardType.name().equals(card.getCardType()))) return false;
            // 5. 획득 층
            if(card.getAcquiredFloor() < 0 || card.getAcquiredFloor() > 9) return false;
        }

        // 6. 보스 페이즈
        List<RankingSource.Phase> phases = record.getBossFight().getPhases();

        if(phases == null)return false;
        if(phases.size() != 3)return false;

        if (phases.get(0).getPhase() != RankingSource.BossPhase.THRONE) return false;
        if (phases.get(1).getPhase() != RankingSource.BossPhase.UNBOUND) return false;
        if (phases.get(2).getPhase() != RankingSource.BossPhase.ECLIPSE) return false;

        if (phases.get(0).getTurns() < 1 || phases.get(1).getTurns() < 1 || phases.get(2).getTurns() < 1) return false;
        int totalTurns = phases.get(0).getTurns() + phases.get(1).getTurns() + phases.get(2).getTurns();
        if (record.getBossFight().getTotalTurns() != totalTurns) return false;


        // 7. 마무리 카드
        boolean isExist = false;
        String finishingCard = record.getBossFight().getFinishingCard();
        for(RankingSource.Card card : record.getDeck().getCards()){
            if(finishingCard.equals(card.getCardType())) {
                isExist = true;
                break;
            }
        }
        if(!isExist) return false;

        return true;
    }

    @Getter
    @AllArgsConstructor
    public class RecordSummary{
        private Long recordId;
        private String playerId;
        private String playerName;
        private int clearTimeSeconds;
        private int remainingHp;
        private int bossTurns;
        private int deckSize;

        public RecordSummary(RankingSource.Record record){
            recordId = record.getId();
            playerId = record.getPlayer().getId();
            playerName = record.getPlayer().getName();
            clearTimeSeconds = record.getRun().getDurationSeconds();
            remainingHp = record.getRun().getFinalHp();
            bossTurns = record.getBossFight().getTotalTurns();
            deckSize = record.getDeck().getSize();
        }

        // 기록 내림차순 정렬 Comparator
        public static Comparator<RecordSummary> getComparator() {
            return Comparator
                    // 1순위: run.durationSeconds 오름차순 (값이 낮을수록 기록이 좋은 것)
                    .comparing(RecordSummary::getClearTimeSeconds)

                    // 2순위: run.finalHp 내림차순
                    .thenComparing(RecordSummary::getRemainingHp, Comparator.reverseOrder())

                    // 3순위: id 오름차순
                    .thenComparing(RecordSummary::getRecordId);
        }
    }
}