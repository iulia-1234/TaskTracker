package com.tracker.tasktracker.controller;

import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.service.UserService;
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
    public UserResponseDto createUser(@RequestBody UserRequestCreateDto userRequestCreateDto) {
        return userService.createUser(userRequestCreateDto);
    }

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

}
