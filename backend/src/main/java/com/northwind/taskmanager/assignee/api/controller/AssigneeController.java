package com.northwind.taskmanager.assignee.api.controller;

import com.northwind.taskmanager.assignee.api.dto.AssigneeResponse;
import com.northwind.taskmanager.assignee.service.AssigneeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assignees")
public class AssigneeController {

    private final AssigneeService assigneeService;

    public AssigneeController(AssigneeService assigneeService) {
        this.assigneeService = assigneeService;
    }

    @GetMapping
    public List<AssigneeResponse> getAssignees() {
        return assigneeService.getAssignees();
    }
}
