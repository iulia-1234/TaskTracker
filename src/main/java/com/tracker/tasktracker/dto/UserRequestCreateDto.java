package com.tracker.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestCreateDto(
        @NotBlank
        String username,

        @Email
        @NotBlank
        String email
) {
}
