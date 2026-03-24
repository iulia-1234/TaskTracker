package com.tracker.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestCreateDto(
        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Email
        @NotBlank
        String email
) {
}
