package com.northwind.taskmanager.task.api.controller;

import com.northwind.taskmanager.task.api.dto.CreateTaskRequest;
import com.northwind.taskmanager.task.api.dto.TaskResponse;
import com.northwind.taskmanager.task.api.dto.UpdateTaskRequest;
import com.northwind.taskmanager.task.api.dto.UpdateTaskStatusRequest;
import com.northwind.taskmanager.task.domain.TaskStatus;
import com.northwind.taskmanager.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String assigneeId
            ) {

        return taskService.getAllTasks(status, assigneeId);
    }

    @GetMapping("/my")
    public List<TaskResponse> getMyTasks(@AuthenticationPrincipal Jwt jwt) {
        return taskService.getTasksForAssignee(jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return taskService.createTask(request,
                jwt.getSubject(), jwt.getClaimAsString("preferred_username"));
    }

    @PatchMapping("/my/{taskId}/status")
    public TaskResponse updateOwnTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.updateTaskStatusForAssignee(
                taskId,
                jwt.getSubject(),
                request.status()
        );
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateAnyTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        return taskService.updateTaskStatusForAssigner(taskId, request.status());
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }


}
