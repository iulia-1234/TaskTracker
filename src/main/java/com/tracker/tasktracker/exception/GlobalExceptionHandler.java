package com.tracker.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

    public String formatErrorCode(Exception exception) {
        return exception.getClass().getSimpleName();
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiExceptions(ApiException apiException) {
        return ResponseEntity
                .status(apiException.getHttpStatus())
                .body(new ErrorResponse(
                        apiException.getMessage(),
                        formatErrorCode(apiException)
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        GENERIC_ERROR_MESSAGE,
                        formatErrorCode(exception)
                ));
    }

}
