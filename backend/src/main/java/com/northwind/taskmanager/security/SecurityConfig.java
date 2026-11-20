package com.northwind.taskmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, KeycloakRoleConverter keycloakRoleConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasRole("assigner")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/my").hasRole("assignee")
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("assigner")
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/my/*/status").hasRole("assignee")
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/*/status").hasRole("assigner")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/*").hasRole("assigner")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/*").hasRole("assigner")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {
                            JwtAuthenticationConverter jwtAuthenticationConverter =
                                    new JwtAuthenticationConverter();

                            jwtAuthenticationConverter
                                    .setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);

                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        }));

        return http.build();
    }
}
