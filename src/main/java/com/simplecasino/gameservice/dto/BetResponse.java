package com.simplecasino.gameservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class BetResponse {

    private Long gameId;

    private List<BigDecimal> bets;

    public BetResponse(Long gameId, List<BigDecimal> bets) {
        this.gameId = gameId;
        this.bets = bets;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<BigDecimal> getBets() {
        return bets;
    }

    public void setBets(List<BigDecimal> bets) {
        this.bets = bets;
    }
}