package com.simplecasino.gameservice.model;

import java.math.BigDecimal;

public class PlayerBet {

    private long playerId;
    private BigDecimal amount;

    public PlayerBet(long playerId, BigDecimal amount) {
        this.playerId = playerId;
        this.amount = amount;
    }

    public long getPlayerId() {
        return playerId;
    }

    private void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
