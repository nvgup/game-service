package com.simplecasino.gameservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "games")
@CompoundIndexes({
        @CompoundIndex(name = "bet_playerid", def = "{ 'playerBets.playerId': 1 }")
})
public class Game {

    @Id
    private Long id;

    @Field("bets")
    private List<PlayerBet> playerBets;

    public Game(Long id) {
        this.id = id;
    }

    private Game() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PlayerBet> getPlayerBets() {
        return playerBets;
    }

    public void setPlayerBets(List<PlayerBet> playerBets) {
        this.playerBets = playerBets;
    }

    public void addPlayerBet(PlayerBet playerBet) {
        if (playerBets == null) {
            playerBets = new ArrayList<>();
        }

        playerBets.add(playerBet);
    }
}
