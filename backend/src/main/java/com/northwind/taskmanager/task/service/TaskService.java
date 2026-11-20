package com.northwind.taskmanager.task.service;

import com.northwind.taskmanager.task.domain.Task;
import com.northwind.taskmanager.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.northwind.taskmanager.task.api.dto.TaskResponse;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksForAssignee(String assigneeId) {
        return taskRepository.findAllByAssigneeIdOrderByCreatedAtDesc(assigneeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssigneeId(),
                task.getAssigneeUsername(),
                task.getCreatedByUsername(),
                task.getCreatedAt()
        );
    }
}
