package com.northwind.taskmanager.assignee.service;

import com.northwind.taskmanager.assignee.api.dto.AssigneeResponse;
import com.northwind.taskmanager.assignee.client.KeycloakAssigneeResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AssigneeService {

    private final RestClient keycloakRestClient;

    public AssigneeService(RestClient keycloakRestClient) {
        this.keycloakRestClient = keycloakRestClient;
    }

    public List<AssigneeResponse> getAssignees() {
        List<KeycloakAssigneeResponse> keycloakAssignees =
                keycloakRestClient.get()
                        .uri("http://localhost:8081/admin/realms/team-task-manager/roles/assignee/users")
                        .attributes(RequestAttributeClientRegistrationIdResolver.clientRegistrationId("keycloak-service"))
                        .attributes(RequestAttributePrincipalResolver.principal("task-manager-backend"))
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        return keycloakAssignees.stream()
                .map(user -> new AssigneeResponse(user.id(), user.username()))
                .toList();
    }
}