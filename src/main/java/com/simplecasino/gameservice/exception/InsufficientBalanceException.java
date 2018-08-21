package com.simplecasino.gameservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    private BigDecimal balance;

    public InsufficientBalanceException(String s, BigDecimal balance) {
        super(s);
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}