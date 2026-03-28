package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.exception.EmailAlreadyExistsException;
import com.tracker.tasktracker.exception.UserNotFoundException;
import com.tracker.tasktracker.exception.UsernameAlreadyExistsException;
import com.tracker.tasktracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestCreateDto userRequestCreateDto) {
        if (userRepository.existsByUsername(userRequestCreateDto.username())) {
            throw new UsernameAlreadyExistsException(userRequestCreateDto.username());
        }
        if (userRepository.existsByEmail(userRequestCreateDto.email())) {
            throw new EmailAlreadyExistsException(userRequestCreateDto.email());
        }
        User user = new User();
        user.setUsername(userRequestCreateDto.username());
        user.setEmail(userRequestCreateDto.email());
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestUpdateDto userRequestUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        String newUsername = userRequestUpdateDto.username();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new UsernameAlreadyExistsException(userRequestUpdateDto.username());
            }
            user.setUsername(userRequestUpdateDto.username());
        }
        String newEmail = userRequestUpdateDto.email();
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException(userRequestUpdateDto.email());
            }
            user.setEmail(userRequestUpdateDto.email());
        }
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
