package com.simplecasino.gameservice.exception;

import org.springframework.http.HttpStatus;

public class RestApiException extends RuntimeException {

    public enum Type {
        GAME_ALREADY_EXIST(HttpStatus.CONFLICT, 40903, "Game already exist"),
        GAME_NOT_FOUND(HttpStatus.NOT_FOUND, 40402, "Game not found");

        private HttpStatus status;
        private int code;
        private String message;

        Type(HttpStatus status, int code, String message) {
            this.status = status;
            this.code = code;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
        }

    private Type type;

    public RestApiException(Type type) {
        this.type = type;
    }

    public RestApiException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
