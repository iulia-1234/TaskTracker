package com.tracker.tasktracker;

import com.tracker.tasktracker.dto.TaskRequestCreateDto;
import com.tracker.tasktracker.dto.TaskRequestUpdateDto;
import com.tracker.tasktracker.dto.TaskResponseDto;
import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import com.tracker.tasktracker.exception.TaskNotFoundException;
import com.tracker.tasktracker.exception.UserNotFoundException;
import com.tracker.tasktracker.repository.TaskRepository;
import com.tracker.tasktracker.repository.UserRepository;
import com.tracker.tasktracker.service.TaskServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplementationTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    TaskServiceImplementation taskServiceImplementation;

    private User user;
    private Task task;
    private TaskRequestCreateDto taskRequestCreateDto;

    @BeforeEach
    public void setUp() {
        user = new User(
                null,
                "Jane",
                "jane@test.com",
                null,
                null,
                null
        );
        task = new Task(
                null,
                "Test Task",
                "Test Task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(3),
                user,
                null,
                null
        );
        taskRequestCreateDto = new TaskRequestCreateDto(
                "Test Task",
                "Test Task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(3),
                user.getId()
        );
    }

    @Test
    void createTask_ShouldCreateTask() {
        when(userRepository.findById(taskRequestCreateDto.userId()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);
        TaskResponseDto taskResponseDto = taskServiceImplementation.createTask(taskRequestCreateDto);
        assertEquals(task.getId(), taskResponseDto.id());
        assertEquals(task.getDescription(), taskResponseDto.description());
        assertEquals(task.getStatus(), taskResponseDto.status());
        assertEquals(task.getPriority(), taskResponseDto.priority());
        assertEquals(task.getDueDate(), taskResponseDto.dueDate());
        assertEquals(task.getUser().getId(), taskResponseDto.userId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(taskRequestCreateDto.userId()))
                .thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> taskServiceImplementation.createTask(taskRequestCreateDto)
        );
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTasks_ShouldReturnTasks() {
        when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(Sort.by("dueDate").ascending())))
                .thenReturn(List.of(task));
        List<TaskResponseDto> tasks = taskServiceImplementation.getTasks(null, null);
        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.getFirst().id());
        assertEquals(task.getDescription(), tasks.getFirst().description());
        assertEquals(task.getStatus(), tasks.getFirst().status());
        assertEquals(task.getPriority(), tasks.getFirst().priority());
        assertEquals(task.getDueDate(), tasks.getFirst().dueDate());
        assertEquals(task.getUser().getId(), tasks.getFirst().userId());
        verify(taskRepository).findAll(ArgumentMatchers.<Specification<Task>>any(), eq(Sort.by("dueDate").ascending()));
    }

    @Test
    void getTask_shouldReturnTask() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        TaskResponseDto taskResponseDto = taskServiceImplementation.getTask(task.getId());
        assertEquals(task.getId(), taskResponseDto.id());
        assertEquals(task.getTitle(), taskResponseDto.title());
        assertEquals(task.getDescription(), taskResponseDto.description());
        assertEquals(task.getStatus(), taskResponseDto.status());
        assertEquals(task.getPriority(), taskResponseDto.priority());
        assertEquals(task.getDueDate(), taskResponseDto.dueDate());
        assertEquals(task.getUser().getId(), taskResponseDto.userId());
        verify(taskRepository).findById(task.getId());
    }

    @Test
    void getTask_shouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.empty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskServiceImplementation.getTask(task.getId())
        );
        verify(taskRepository).findById(task.getId());
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        TaskRequestUpdateDto taskRequestUpdateDto = new TaskRequestUpdateDto(
                "Test Task edited",
                "Test Task description edited",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDateTime.now().plusDays(5),
                user.getId()
        );
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);
        TaskResponseDto taskResponseDto =  taskServiceImplementation.updateTask(task.getId(), taskRequestUpdateDto);
        assertEquals(task.getTitle(), taskResponseDto.title());
        assertEquals(task.getDescription(), taskResponseDto.description());
        assertEquals(task.getStatus(), taskResponseDto.status());
        assertEquals(task.getPriority(), taskResponseDto.priority());
        assertEquals(task.getDueDate(), taskResponseDto.dueDate());
        assertEquals(task.getUser().getId(), taskResponseDto.userId());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.empty());
        TaskRequestUpdateDto taskRequestUpdateDto = new TaskRequestUpdateDto(
                "Test Task edited",
                "Test Task description edited",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDateTime.now().plusDays(5),
                user.getId()
        );
        assertThrows(
                TaskNotFoundException.class,
                () -> taskServiceImplementation.updateTask(task.getId(), taskRequestUpdateDto)
        );
        verify(taskRepository, never()).save(task);
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);
        taskServiceImplementation.deleteTask(task.getId());
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.empty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskServiceImplementation.deleteTask(task.getId())
        );
        verify(taskRepository, never()).delete(task);
    }
}
