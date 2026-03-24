package com.tracker.tasktracker.dto;

import jakarta.validation.constraints.Email;

public record UserRequestUpdateDto(
        String username,
        String password,

        @Email
        String email
) {
}
