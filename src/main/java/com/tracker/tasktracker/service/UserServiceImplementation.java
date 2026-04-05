package com.tracker.tasktracker.service;

import com.tracker.tasktracker.dto.TaskSummaryDto;
import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import com.tracker.tasktracker.exception.EmailAlreadyExistsException;
import com.tracker.tasktracker.exception.UserNotFoundException;
import com.tracker.tasktracker.exception.UsernameAlreadyExistsException;
import com.tracker.tasktracker.repository.TaskRepository;
import com.tracker.tasktracker.repository.UserRepository;
import com.tracker.tasktracker.specification.TaskSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserServiceImplementation(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    private TaskSummaryDto toTaskSummaryDto(Task task) {
        return new TaskSummaryDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private UserResponseDto toDto(User user) {
        List<TaskSummaryDto> tasks = user.getTasks() == null
                ? List.of()
                : user.getTasks().stream()
                .map(this::toTaskSummaryDto)
                .toList();
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                tasks
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
        return toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toDto(user);
    }

    @Override
    public List<TaskSummaryDto> getUserTasks(Long id, TaskStatus status, TaskPriority priority) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        Specification<Task> specification = Specification
                .where(TaskSpecifications.hasUserId(id))
                .and(TaskSpecifications.hasStatus(status))
                .and(TaskSpecifications.hasPriority(priority));
        return taskRepository.findAll(specification, Sort.by("dueDate").ascending())
                .stream()
                .map(this::toTaskSummaryDto)
                .toList();
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
