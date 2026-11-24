package com.northwind.taskmanager.task.api.dto;

import com.northwind.taskmanager.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 150) String title,
        String description,
        @NotNull TaskPriority priority,
        @NotNull LocalDate dueDate,
        @NotBlank String assigneeId
        ) {
}
