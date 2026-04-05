package com.tracker.tasktracker.dto;

import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequestCreateDto(
        @NotBlank
        String title,
        String description,

        @NotNull
        TaskPriority priority,

        @NotNull
        TaskStatus status,

        LocalDateTime dueDate,

        @NotNull
        Long userId
) {
}
