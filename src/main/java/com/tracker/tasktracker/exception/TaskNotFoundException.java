package com.tracker.tasktracker.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends ApiException {

    public TaskNotFoundException(Long id) {
        super("Task with id " +  id + " is not found");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
