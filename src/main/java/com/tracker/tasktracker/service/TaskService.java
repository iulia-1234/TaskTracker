package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.TaskRequestCreateDto;
import com.tracker.tasktracker.dto.TaskRequestUpdateDto;
import com.tracker.tasktracker.dto.TaskResponseDto;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestCreateDto taskRequestCreateDto);
    List<TaskResponseDto> getTasks(Long userId, TaskStatus status, TaskPriority priority);
    TaskResponseDto getTask(Long id);
    TaskResponseDto updateTask(Long id, TaskRequestUpdateDto taskRequestUpdateDto);
    void deleteTask(Long id);
}
