package com.tracker.tasktracker.specification;

import com.tracker.tasktracker.entity.Task;
import com.tracker.tasktracker.enums.TaskPriority;
import com.tracker.tasktracker.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

    public static Specification<Task> hasUserId(Long userId) {
        return (root, criteriaQuery, criteriaBuilder)
                -> userId == null ? null : criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, criteriaQuery, criteriaBuilder)
                -> status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, criteriaQuery, criteriaBuilder)
                -> priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority);
    }
}
