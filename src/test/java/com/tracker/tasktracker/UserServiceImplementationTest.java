package com.tracker.tasktracker;

import com.tracker.tasktracker.dto.TaskSummaryDto;
import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.dto.UserResponseDto;
import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.exception.EmailAlreadyExistsException;
import com.tracker.tasktracker.exception.UserNotFoundException;
import com.tracker.tasktracker.exception.UsernameAlreadyExistsException;
import com.tracker.tasktracker.repository.TaskRepository;
import com.tracker.tasktracker.repository.UserRepository;
import com.tracker.tasktracker.service.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserServiceImplementation userServiceImplementation;

    private User user;
    private UserRequestCreateDto userRequestCreateDto;

    @BeforeEach
    public void setUp() {
        user = new User(
                1L,
                "Jane",
                "jane@test.com",
                null,
                null,
                null
        );

        userRequestCreateDto = new UserRequestCreateDto(
                "Jane",
                "jane@test.com"
        );
    }

    @Test
    void createUser_ShouldSaveUser() {
        when(userRepository.existsByUsername(userRequestCreateDto.username()))
                .thenReturn(false);
        when(userRepository.existsByEmail(userRequestCreateDto.email()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserResponseDto userResponseDto = userServiceImplementation.createUser(userRequestCreateDto);
        assertEquals(user.getId(), userResponseDto.id());
        assertEquals(user.getUsername(), userResponseDto.username());
        assertEquals(user.getEmail(), userResponseDto.email());
        verify(userRepository).existsByUsername(userRequestCreateDto.username());
        verify(userRepository).existsByEmail(userRequestCreateDto.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(userRequestCreateDto.username()))
                .thenReturn(true);
        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userServiceImplementation.createUser(userRequestCreateDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(userRequestCreateDto.username()))
                .thenReturn(false);
        when(userRepository.existsByEmail(userRequestCreateDto.email()))
                .thenReturn(true);
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userServiceImplementation.createUser(userRequestCreateDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserResponseDto> users = userServiceImplementation.getUsers();
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.getFirst().id());
        assertEquals(user.getUsername(), users.getFirst().username());
        assertEquals(user.getEmail(), users.getFirst().email());
        verify(userRepository).findAll();
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        UserResponseDto userResponseDto = userServiceImplementation.getUser(user.getId());
        assertEquals(user.getId(), userResponseDto.id());
        assertEquals(user.getUsername(), userResponseDto.username());
        assertEquals(user.getEmail(), userResponseDto.email());
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImplementation.getUser(user.getId())
        );
        verify(userRepository).findById(user.getId());
    }

    @Test
    void getUserTasks_ShouldReturnAllUserTasks() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        when(userRepository.existsById(user.getId()))
                .thenReturn(true);
        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), ArgumentMatchers.any(Sort.class)))
                .thenReturn(List.of(task));
        List<TaskSummaryDto> userTasks = userServiceImplementation.getUserTasks(user.getId(), null, null);
        assertEquals(1, userTasks.size());
        assertEquals(task.getId(), userTasks.getFirst().id());
        assertEquals(task.getTitle(), userTasks.getFirst().title());
        verify(taskRepository).findAll(ArgumentMatchers.<Specification<Task>>any(), eq(Sort.by("dueDate").ascending()));
    }

    @Test
    void getUserTasks_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(user.getId()))
                .thenReturn(false);
        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImplementation.getUserTasks(user.getId(), null, null)
        );
        verify(taskRepository, never()).findAll();
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto("Jane_2", "jane_2@test.com");
        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateUsernameOnly() {
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto("Jane_2", null);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);
        UserResponseDto userResponseDto = userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto);
        assertEquals(user.getId(), userResponseDto.id());
        assertEquals(userRequestUpdateDto.username(), userResponseDto.username());
        assertEquals(user.getEmail(), userResponseDto.email());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateEmailOnly() {
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(null, "jane_2@test.com");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);
        UserResponseDto userResponseDto = userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto);
        assertEquals(user.getId(), userResponseDto.id());
        assertEquals(user.getUsername(), userResponseDto.username());
        assertEquals(userRequestUpdateDto.email(), userResponseDto.email());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateUsernameAndEmail() {
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto("Jane_2", "jane_2@test.com");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);
        UserResponseDto userResponseDto = userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto);
        assertEquals(user.getId(), userResponseDto.id());
        assertEquals(userRequestUpdateDto.username(), userResponseDto.username());
        assertEquals(userRequestUpdateDto.email(), userResponseDto.email());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto("Jane_2", null);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(userRequestUpdateDto.username()))
                .thenReturn(true);
        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(null, "jane_2@test.com");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userRequestUpdateDto.email()))
                .thenReturn(true);
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userServiceImplementation.updateUser(user.getId(), userRequestUpdateDto)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImplementation.deleteUser(user.getId())
        );
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userServiceImplementation.deleteUser(user.getId());
        verify(userRepository).delete(user);
    }
}
