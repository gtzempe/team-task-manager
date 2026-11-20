package com.northwind.taskmanager.task.api.dto;

import com.northwind.taskmanager.task.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull TaskStatus status
        ) {
}
