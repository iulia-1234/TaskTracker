package com.tracker.tasktracker.entity;

import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    // ===== ID =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Core fields =====
    @Column(nullable = false)
    private String title;
    private String description;

    // ===== Enums =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    // ===== Timestamps =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;

    // ===== Relationships =====
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ===== Lifecycle hooks =====
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
