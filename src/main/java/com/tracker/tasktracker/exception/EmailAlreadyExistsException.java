package com.tracker.tasktracker.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends ApiException {

    public EmailAlreadyExistsException(String email) {
        super("Email " +  email + " is already in use");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
