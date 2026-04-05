package com.tracker.tasktracker.dto;

import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskSummaryDto(
        Long id,
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
