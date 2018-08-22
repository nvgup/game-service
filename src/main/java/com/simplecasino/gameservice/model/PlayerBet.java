package com.simplecasino.gameservice.model;

import java.math.BigDecimal;

public class PlayerBet {

    private Long playerId;
    private BigDecimal amount;

    public PlayerBet(Long playerId, BigDecimal amount) {
        this.playerId = playerId;
        this.amount = amount;
    }

    public Long getPlayerId() {
        return playerId;
    }

    private void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
