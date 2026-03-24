package com.tracker.tasktracker.dto;

public record UserResponseDto(
        Long id,
        String username,
        String email
) {
}
