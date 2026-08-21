package org.example.internship_order_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.internship_order_service.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient userServiceRestClient;

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallbackGetUser")
    public UserDTO getUserById(Long id) {
        return userServiceRestClient.get()
                .uri("/api/internal/users/{id}", id)
                .retrieve()
                .body(UserDTO.class);
    }

    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallbackGetUser")
    public UserDTO getUserByAuthUserId(Long authUserId) {
        return userServiceRestClient.get()
                .uri("/api/internal/users/auth/{authUserId}", authUserId)
                .retrieve()
                .body(UserDTO.class);
    }

    private UserDTO fallbackGetUser(Long id, Throwable t) {
        log.warn("User Service unavailable, returning order without user info. userId={}, cause={}",
                id, t.getMessage());
        return null;
    }
}
