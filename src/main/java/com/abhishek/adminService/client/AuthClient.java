package com.abhishek.adminService.client;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestClient restClient;

    @Value("${auth-service.url:http://localhost:8081/auth}")
    private String authServiceUrl;

    public Map<String, UserDTO> fetchUsersMap(String bearerToken) {
        log.debug("Fetching all users from auth-service");
        try {
            ApiResponse<List<UserDTO>> response = restClient.get()
                    .uri(authServiceUrl + "/users")
                    .header("Authorization", "Bearer " + bearerToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .collect(Collectors.toMap(UserDTO::getId, user -> user));
            }
        } catch (Exception e) {
            log.error("Failed to fetch users from auth-service", e);
        }
        return Map.of();
    }
}
