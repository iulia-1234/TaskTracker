package com.tracker.tasktracker.service;

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
import com.tracker.tasktracker.specification.TaskSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImplementation implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImplementation(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getUser().getId(),
                task.getUser().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestCreateDto taskRequestCreateDto) {
        Task task = new Task();
        task.setTitle(taskRequestCreateDto.title());
        task.setDescription(taskRequestCreateDto.description());
        task.setPriority(taskRequestCreateDto.priority());
        task.setStatus(taskRequestCreateDto.status());
        task.setDueDate(taskRequestCreateDto.dueDate());
        User user = userRepository.findById(taskRequestCreateDto.userId())
                .orElseThrow( () -> new UserNotFoundException(taskRequestCreateDto.userId()));
        task.setUser(user);
        return  toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDto> getTasks(TaskStatus status, TaskPriority priority) {
        Specification<Task> specification = Specification
                .where(TaskSpecifications.hasStatus(status))
                .and(TaskSpecifications.hasPriority(priority));
        List<Task> tasks = taskRepository.findAll(
                specification,
                Sort.by("dueDate").ascending()
        );
        return tasks.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public TaskResponseDto getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow( () -> new TaskNotFoundException(id));
        return toDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestUpdateDto taskRequestUpdateDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow( () -> new TaskNotFoundException(id));
        if (taskRequestUpdateDto.title() != null) {
            task.setTitle(taskRequestUpdateDto.title());
        }
        if (taskRequestUpdateDto.description() != null) {
            task.setDescription(taskRequestUpdateDto.description());
        }
        if (taskRequestUpdateDto.priority() != null) {
            task.setPriority(taskRequestUpdateDto.priority());
        }
        if (taskRequestUpdateDto.status() != null) {
            task.setStatus(taskRequestUpdateDto.status());
        }
        if (taskRequestUpdateDto.dueDate() != null) {
            task.setDueDate(taskRequestUpdateDto.dueDate());
        }
        if (taskRequestUpdateDto.userId() != null) {
            User user = userRepository.findById(taskRequestUpdateDto.userId())
                    .orElseThrow( () -> new UserNotFoundException(taskRequestUpdateDto.userId()));
            task.setUser(user);
        }
        return toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow( () -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }
}
