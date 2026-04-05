package com.tracker.tasktracker.dto;

import java.util.List;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        List<TaskSummaryDto> tasks
) {
}
