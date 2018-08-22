package com.simplecasino.gameservice.dto;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class PlaceBetRequest {

    private Long playerId;

    @Min(0)
    private BigDecimal amount;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
