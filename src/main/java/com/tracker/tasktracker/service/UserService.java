package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestCreateDto userRequestCreateDto);
    List<UserResponseDto> getUsers();
    UserResponseDto getUser(Long id);
    UserResponseDto updateUser(Long id, UserRequestUpdateDto userRequestUpdateDto);
    void deleteUser(Long id);
}
