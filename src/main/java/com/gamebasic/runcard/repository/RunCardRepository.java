package com.gamebasic.runcard.repository;

import com.gamebasic.runcard.dto.DeckCount;
import com.gamebasic.game.entity.Game;
import com.gamebasic.runcard.dto.CardResponse;
import com.gamebasic.runcard.entity.RunCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RunCardRepository extends JpaRepository<RunCard, Long> {
    List<RunCard> findAllByGameOrderByIdAsc(Game game);

    @Modifying
    @Query("delete from RunCard r where r.game = :game")
    void deleteAllByGame(Game game);

    List<CardResponse> findAllByGameIdOrderByIdAsc(Long gameId);

    // TODO (Lv 11): @Query 작성
    @Query("select new com.gamebasic.runcard.dto.DeckCount(g.id, count(r)) from Game g left join RunCard r on r.game = g where g in :games group by g.id")
    List<DeckCount> countByGames(List<Game> games);
}
