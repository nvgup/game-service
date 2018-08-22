package com.simplecasino.gameservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public class ApiError<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private int code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T additionalInfo;

    private ApiError() {

    }

    public ApiError(RestApiException.Type exType, T additionalInfo) {
        this(exType.getStatus().value(), exType.getCode(), exType.getMessage());
        this.additionalInfo = additionalInfo;
    }

    public ApiError(RestApiException.Type exType) {
        this(exType.getStatus().value(), exType.getCode(), exType.getMessage());
    }

    public ApiError(int status, int code, String message) {
        timestamp = LocalDateTime.now();

        this.status = status;
        this.code = code;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(T additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
