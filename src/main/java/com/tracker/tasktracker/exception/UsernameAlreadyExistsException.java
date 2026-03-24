package com.tracker.tasktracker.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ApiException {

    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " is already taken");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
