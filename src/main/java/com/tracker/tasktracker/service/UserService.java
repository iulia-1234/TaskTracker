package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.TaskSummaryDto;
import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestCreateDto userRequestCreateDto);
    List<UserResponseDto> getUsers();
    UserResponseDto getUser(Long id);
    List<TaskSummaryDto> getUserTasks(Long id, TaskStatus status, TaskPriority priority);
    UserResponseDto updateUser(Long id, UserRequestUpdateDto userRequestUpdateDto);
    void deleteUser(Long id);
}
