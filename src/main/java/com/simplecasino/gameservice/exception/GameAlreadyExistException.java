package com.simplecasino.gameservice.exception;

public class GameAlreadyExistException extends RuntimeException {

    public GameAlreadyExistException(String s) {
        super(s);
    }
}