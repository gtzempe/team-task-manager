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

    public TaskResponse updateTaskStatusForAssignee(
            Long taskId,
            String assigneeId,
            TaskStatus newStatus
    ) {
        Task task = taskRepository.findByIdAndAssigneeId(taskId, assigneeId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
                );

        if (!isValidStatusTransition(task.getStatus(), newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition"
            );
        }

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        return toResponse(updatedTask);
    }

    public TaskResponse updateTaskStatusForAssigner(
            Long taskId,
            TaskStatus newStatus
    ) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
                );

        task.setStatus(newStatus);

        return toResponse(taskRepository.save(task));
    }

    private boolean isValidStatusTransition(
            TaskStatus currentStatus,
            TaskStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return true;
        }

        return switch (currentStatus) {
            case TODO ->
                    newStatus == TaskStatus.IN_PROGRESS
                            || newStatus == TaskStatus.BLOCKED;
            case IN_PROGRESS ->
                    newStatus == TaskStatus.DONE
                            || newStatus == TaskStatus.BLOCKED;
            case BLOCKED -> newStatus == TaskStatus.IN_PROGRESS;
            case DONE -> false;
        };
    }
}







