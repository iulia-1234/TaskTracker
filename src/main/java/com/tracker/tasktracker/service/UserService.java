package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.exception.EmailAlreadyExistsException;
import com.tracker.tasktracker.exception.UsernameAlreadyExistsException;
import com.tracker.tasktracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    private void validateUser(UserRequestCreateDto userRequestCreateDto) {
        if (userRepository.existsByUsername(userRequestCreateDto.username())) {
            throw new UsernameAlreadyExistsException(userRequestCreateDto.username());
        }
        if (userRepository.existsByEmail(userRequestCreateDto.email())) {
            throw new EmailAlreadyExistsException(userRequestCreateDto.email());
        }
    }

    @Transactional
    public UserResponseDto createUser(UserRequestCreateDto userRequestCreateDto) {
        // ==== Validate user ====
        validateUser(userRequestCreateDto);

        // ==== Create and save user ====
        User user = new User();
        user.setUsername(userRequestCreateDto.username());
        user.setPassword(passwordEncoder.encode(userRequestCreateDto.password()));
        user.setEmail(userRequestCreateDto.email());
        User savedUser = userRepository.save(user);

        // ==== Return user mapped to dto ====
        return toDto(savedUser);
    }

    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
