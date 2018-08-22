package com.simplecasino.gameservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Optional;

public class BalanceResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    private BigDecimal balance;

    private BalanceResponse() {
    }

    public BalanceResponse(String error, BigDecimal balance) {
        this(balance);
        this.error = error;
    }

    public BalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
