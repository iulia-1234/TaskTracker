package com.tracker.tasktracker.dto;

import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskRequestUpdateDto(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate,
        Long userId
) {
}
