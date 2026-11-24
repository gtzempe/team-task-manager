package com.northwind.taskmanager.assignee.service;

import com.northwind.taskmanager.assignee.api.dto.AssigneeResponse;
import com.northwind.taskmanager.assignee.client.KeycloakAssigneeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AssigneeService {

    private final RestClient keycloakRestClient;
    private final String assigneeUsersUrl;

    public AssigneeService(RestClient keycloakRestClient,
                           @Value("${keycloak.assignee-users-url}") String assigneeUsersUrl) {
        this.keycloakRestClient = keycloakRestClient;
        this.assigneeUsersUrl = assigneeUsersUrl;
    }

    public List<AssigneeResponse> getAssignees() {
        List<KeycloakAssigneeResponse> keycloakAssignees =
                keycloakRestClient.get()
                        .uri(assigneeUsersUrl)
                        .attributes(RequestAttributeClientRegistrationIdResolver.clientRegistrationId("keycloak-service"))
                        .attributes(RequestAttributePrincipalResolver.principal("task-manager-backend"))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        return keycloakAssignees.stream()
                .map(user -> new AssigneeResponse(user.id(), user.username()))
                .toList();
    }

    public AssigneeResponse getAssigneeById(String id) {
        return getAssignees().stream()
                .filter(assignee -> assignee.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid assignee")
                );
    }
}