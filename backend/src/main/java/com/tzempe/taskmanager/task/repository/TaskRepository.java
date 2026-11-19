package com.tzempe.taskmanager.task.repository;

import com.tzempe.taskmanager.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findAllByAssigneeIdOrderByCreatedAtDesc(String assigneeId);
}
