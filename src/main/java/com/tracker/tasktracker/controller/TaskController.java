package com.tracker.tasktracker.controller;

import com.tracker.tasktracker.dto.TaskRequestCreateDto;
import com.tracker.tasktracker.dto.TaskRequestUpdateDto;
import com.tracker.tasktracker.dto.TaskResponseDto;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import com.tracker.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {
    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody TaskRequestCreateDto taskRequestCreateDto) {
        return taskService.createTask(taskRequestCreateDto);
    }

    @GetMapping
    public List<TaskResponseDto> getTasks(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false)TaskPriority priority
            ) {
        return taskService.getTasks(userId, status, priority);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PatchMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestUpdateDto taskRequestUpdateDto) {
        return taskService.updateTask(id, taskRequestUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
