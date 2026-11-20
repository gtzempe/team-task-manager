package com.northwind.taskmanager.task.repository;

import com.northwind.taskmanager.task.domain.Task;
import com.northwind.taskmanager.task.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findAllByAssigneeIdOrderByCreatedAtDesc(String assigneeId);

    Optional<Task> findByIdAndAssigneeId(Long id, String assigneeId);

    List<Task> findAllByStatusOrderByCreatedAtDesc(TaskStatus status);

    List<Task> findAllByStatusAndAssigneeIdOrderByCreatedAtDesc(
            TaskStatus status,
            String assigneeId
    );
}
