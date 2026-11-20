package com.northwind.taskmanager.task.api.dto;

import com.northwind.taskmanager.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        @NotNull TaskPriority priority,
        @NotNull LocalDate dueDate,
        @NotBlank String assigneeId,
        @NotBlank String assigneeUsername
) {
}
