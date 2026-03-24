package com.tracker.tasktracker.exception;

public record ErrorResponse(
        String message,
        String errorCode
) {
}
