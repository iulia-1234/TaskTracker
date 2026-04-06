package com.tracker.tasktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.tasktracker.dto.TaskRequestCreateDto;
import com.tracker.tasktracker.dto.TaskRequestUpdateDto;
import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import com.tracker.tasktracker.repository.TaskRepository;
import com.tracker.tasktracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturn201_WhenTaskIsCreated() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        TaskRequestCreateDto taskRequestCreateDto = new TaskRequestCreateDto(
                "Test Task",
                "Test Task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(3),
                user.getId()
        );
        mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(taskRequestCreateDto.title()))
                .andExpect(jsonPath("$.description").value(taskRequestCreateDto.description()))
                .andExpect(jsonPath("$.status").value(taskRequestCreateDto.status().toString()))
                .andExpect(jsonPath("$.priority").value(taskRequestCreateDto.priority().toString()))
                .andExpect(jsonPath("$.dueDate").value(String.valueOf(taskRequestCreateDto.dueDate())))
                .andExpect(jsonPath("$.userId").value(taskRequestCreateDto.userId().toString()));
        assertEquals(1, taskRepository.count());
    }

    @Test
    void createTask_ShouldReturn400_WhenTitleIsMissing() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        TaskRequestCreateDto taskRequestCreateDto = new TaskRequestCreateDto(
                "",
                "Test Task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(3),
                user.getId()
        );
        mockMvc.perform(post("/api/tasks")
                        .contentType(String.valueOf(org.junit.jupiter.api.MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(taskRequestCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title: must not be blank"))
                .andExpect(jsonPath("$.errorCode").value("MethodArgumentNotValidException"));
    }

    @Test
    void createTask_ShouldReturn404_WhenUserIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        TaskRequestCreateDto taskRequestCreateDto = new TaskRequestCreateDto(
                "Test Task",
                "Test Task description",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(3),
                0L
        );
        mockMvc.perform(post("/api/tasks")
                        .contentType(String.valueOf(org.junit.jupiter.api.MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(taskRequestCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("UserNotFoundException"));
    }

    @Test
    void getTasks_ShouldReturn200_WhenTasksAreFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].title").value(task.getTitle()))
                .andExpect(jsonPath("$.[0].description").value(task.getDescription()))
                .andExpect(jsonPath("$.[0].status").value(task.getStatus().toString()))
                .andExpect(jsonPath("$.[0].priority").value(task.getPriority().toString()))
                .andExpect(jsonPath("$.[0].dueDate").value(String.valueOf(task.getDueDate())))
                .andExpect(jsonPath("$.[0].username").value(task.getUser().getUsername()));
    }

    @Test
    void getTask_ShouldReturn200_WhenTaskIsFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        mockMvc.perform(get("/api/tasks/" +  task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value(task.getStatus().toString()))
                .andExpect(jsonPath("$.priority").value(task.getPriority().toString()))
                .andExpect(jsonPath("$.dueDate").value(String.valueOf(task.getDueDate())))
                .andExpect(jsonPath("$.username").value(task.getUser().getUsername()));
    }

    @Test
    void getTask_ShouldReturn404_WhenTaskIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        mockMvc.perform(get("/api/tasks/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("TaskNotFoundException"));

    }

    @Test
    void updateTask_ShouldReturn200_WhenTaskIsUpdated() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        TaskRequestUpdateDto taskRequestUpdateDto = new TaskRequestUpdateDto(
                "Test Task edited",
                "Test Task description edited",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDateTime.now().plusDays(5),
                user.getId()
        );
        mockMvc.perform(patch("/api/tasks/" + task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequestUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(taskRequestUpdateDto.title()))
                .andExpect(jsonPath("$.description").value(taskRequestUpdateDto.description()))
                .andExpect(jsonPath("$.status").value(taskRequestUpdateDto.status().toString()))
                .andExpect(jsonPath("$.priority").value(taskRequestUpdateDto.priority().toString()))
                .andExpect(jsonPath("$.dueDate").value(String.valueOf(taskRequestUpdateDto.dueDate())));
    }

    @Test
    void updateTask_ShouldReturn404_WhenTaskIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        TaskRequestUpdateDto taskRequestUpdateDto = new TaskRequestUpdateDto(
                "Test Task edited",
                "Test Task description edited",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDateTime.now().plusDays(5),
                user.getId()
        );
        mockMvc.perform(patch("/api/tasks/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequestUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("TaskNotFoundException"));
    }

    @Test
    void deleteTask_ShouldReturn204_WhenTaskIsDeleted() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        mockMvc.perform(delete("/api/tasks/" + task.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_ShouldReturn404_WhenTaskIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(
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
        taskRepository.save(task);
        mockMvc.perform(delete("/api/tasks/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("TaskNotFoundException"));
    }
}
