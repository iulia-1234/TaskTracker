package com.tracker.tasktracker.controller;

import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserRequestCreateDto userRequestCreateDto) {
        return userService.createUser(userRequestCreateDto);
    }

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestUpdateDto userRequestUpdateDto) {
        return userService.updateUser(id, userRequestUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
