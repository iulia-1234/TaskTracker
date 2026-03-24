package com.tracker.tasktracker.repository;

import com.tracker.tasktracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
