package com.northwind.taskmanager.task.service;

import com.northwind.taskmanager.task.api.dto.CreateTaskRequest;
import com.northwind.taskmanager.task.domain.Task;
import com.northwind.taskmanager.task.domain.TaskStatus;
import com.northwind.taskmanager.task.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.northwind.taskmanager.task.api.dto.TaskResponse;
import org.springframework.web.server.ResponseStatusException;

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

    public TaskResponse createTask(
            CreateTaskRequest request,
            String createdById,
            String createdByUsername
    ) {
        Task task = new Task(
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate(),
                request.assigneeId(),
                request.assigneeUsername(),
                createdById,
                createdByUsername
        );

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public TaskResponse updateTaskStatus(
            Long taskId,
            String assigneeId,
            TaskStatus newStatus
    ) {
        Task task = taskRepository.findByIdAndAssigneeId(taskId, assigneeId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
                );

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        return toResponse(updatedTask);
    }
}







