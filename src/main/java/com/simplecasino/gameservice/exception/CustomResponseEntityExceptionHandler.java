package com.simplecasino.gameservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RestApiException.class})
    protected ResponseEntity<?> handleRestApiException(RestApiException ex, WebRequest webRequest) {
        ApiError apiError = new ApiError(ex.getType());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(),
                ex.getType().getStatus(), webRequest);
    }
}