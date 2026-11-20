package com.northwind.taskmanager.task.api;

import com.northwind.taskmanager.task.api.dto.CreateTaskRequest;
import com.northwind.taskmanager.task.api.dto.TaskResponse;
import com.northwind.taskmanager.task.api.dto.UpdateTaskStatusRequest;
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
    public List<TaskResponse> getAllTasks() {

        return taskService.getAllTasks();
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

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.updateTaskStatus(
                taskId,
                jwt.getSubject(),
                request.status()
        );
    }


}
