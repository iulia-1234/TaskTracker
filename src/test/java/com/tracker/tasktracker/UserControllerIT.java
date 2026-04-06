package com.tracker.tasktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.tasktracker.dto.UserRequestCreateDto;
import com.tracker.tasktracker.dto.UserRequestUpdateDto;
import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.entity.User;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import com.tracker.tasktracker.repository.TaskRepository;
import com.tracker.tasktracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturn201_WhenUserIsCreated() throws Exception {
        UserRequestCreateDto userRequestCreateDto = new UserRequestCreateDto(
                "Jane",
                "jane@test.com"
        );
        mockMvc.perform(post("/api/users")
                    .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                    .content(objectMapper.writeValueAsString(userRequestCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane@test.com"));
        assertEquals(1, userRepository.count());
    }

    @Test
    void createUser_ShouldReturn400_WhenUsernameIsMissing() throws Exception {
        UserRequestCreateDto userRequestCreateDto = new UserRequestCreateDto(
                "",
                "jane@test.com"
        );
        mockMvc.perform(post("/api/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(userRequestCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username: must not be blank"))
                .andExpect(jsonPath("$.errorCode").value("MethodArgumentNotValidException"));
    }

    @Test
    void createUser_ShouldReturn409_WhenUsernameAlreadyExists() throws Exception {
        User existingUser = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(existingUser);
        UserRequestCreateDto userRequestCreateDto = new UserRequestCreateDto(
                "Jane",
                "jane@test.com"
        );
        mockMvc.perform(post("/api/users")
                    .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                    .content(objectMapper.writeValueAsString(userRequestCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username Jane already exists"))
                .andExpect(jsonPath("$.errorCode").value("UsernameAlreadyExistsException"));
    }

    @Test
    void getUsers_ShouldReturn200_WhenUsersAreFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    void getUser_ShouldReturn200_WhenUserIsFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }

    @Test
    void getUser_ShouldReturn404_WhenUserIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("UserNotFoundException"));
    }

    @Test
    void getUserTasks_ShouldReturn200_WhenUserIsFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(null, "Test Task", null, TaskStatus.TODO, TaskPriority.MEDIUM, null, user, null, null);
        taskRepository.save(task);
        mockMvc.perform(get("/api/users/" + user.getId() + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[0].priority").value("MEDIUM"));
    }

    @Test
    void getUserTasks_ShouldReturn404_WhenUserIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        Task task = new Task(null, "Test Task", null, TaskStatus.TODO, TaskPriority.MEDIUM, null, user, null, null);
        taskRepository.save(task);
        mockMvc.perform(get("/api/users/0/tasks"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("UserNotFoundException"));
    }

    @Test
    void updateUser_ShouldReturn200_WhenUserIsUpdated() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(
                "Jane2",
                "jane2@test.com"
        );
        mockMvc.perform(patch("/api/users/" + user.getId())
                    .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                    .content(objectMapper.writeValueAsString(userRequestUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userRequestUpdateDto.username()))
                .andExpect(jsonPath("$.email").value(userRequestUpdateDto.email()));
    }

    @Test
    void updateUser_ShouldReturn400_WhenUserEmailIsInvalid() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(
                "Jane2",
                "invalidEmailAddress"
        );
        mockMvc.perform(patch("/api/users/" + user.getId())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(userRequestUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: must be a well-formed email address"))
                .andExpect(jsonPath("$.errorCode").value("MethodArgumentNotValidException"));
    }

    @Test
    void updateUser_ShouldReturn404_WhenUserIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(
                "Jane2",
                "jane2@test.com"
        );
        mockMvc.perform(patch("/api/users/0")
                    .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                    .content(objectMapper.writeValueAsString(userRequestUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("UserNotFoundException"));
    }

    @Test
    void updateUser_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        User existingUser1 = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(existingUser1);
        User existingUser2 = new User(null, "Jane2", "jane2@test.com", null, null, null);
        userRepository.save(existingUser2);
        UserRequestUpdateDto userRequestUpdateDto = new UserRequestUpdateDto(
                "Jane2_2",
                "jane@test.com"
        );
        mockMvc.perform(patch("/api/users/" + existingUser2.getId())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(userRequestUpdateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email jane@test.com already exists"))
                .andExpect(jsonPath("$.errorCode").value("EmailAlreadyExistsException"));

    }

    @Test
    void deleteUser_ShouldReturn204_WhenUserIsDeleted() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturn404_WhenUserIsNotFound() throws Exception {
        User user = new User(null, "Jane", "jane@test.com", null, null, null);
        userRepository.save(user);
        mockMvc.perform(delete("/api/users/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 0 is not found"))
                .andExpect(jsonPath("$.errorCode").value("UserNotFoundException"));
    }
}
