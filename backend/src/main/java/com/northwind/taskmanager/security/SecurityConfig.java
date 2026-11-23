package com.northwind.taskmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            KeycloakRoleConverter keycloakRoleConverter,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasRole("assigner")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/my").hasRole("assignee")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*").hasRole("assigner")
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("assigner")
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/my/*/status").hasRole("assignee")
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/*/status").hasRole("assigner")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/*").hasRole("assigner")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/*").hasRole("assigner")
                        .requestMatchers(HttpMethod.GET, "/api/assignees").hasRole("assigner")
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

}






