package com.northwind.taskmanager.task.api.dto;

import com.northwind.taskmanager.task.domain.TaskPriority;
import com.northwind.taskmanager.task.domain.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        String assigneeId,
        String assigneeUsername,
        String createdByUsername,
        Instant createdAt
) {
}
