package com.tzempe.taskmanager.task.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    private String assigneeId;

    @Column(nullable = false)
    private String assigneeUsername;

    @Column(nullable = false)
    private String createdById;

    @Column(nullable = false)
    private String createdByUsername;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    protected Task() {

    }

    public Task(
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate,
            String assigneeId,
            String assigneeUsername,
            String createdById,
            String createdByUsername
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assigneeId = assigneeId;
        this.assigneeUsername = assigneeUsername;
        this.createdById = createdById;
        this.createdByUsername = createdByUsername;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setAssigneeUsername(String assigneeUsername) {
        this.assigneeUsername = assigneeUsername;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getAssigneeUsername() {
        return assigneeUsername;
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
