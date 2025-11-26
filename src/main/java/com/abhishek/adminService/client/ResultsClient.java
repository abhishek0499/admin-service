package com.abhishek.adminService.client;

import com.abhishek.adminService.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResultsClient {
    private final RestClient restClient;
    private final String RESULTS_SERVICE_URL = "http://localhost:8084/results";

    public List<Map<String, Object>> getTestResults(String testId, String bearerToken) {
        try {
            var req = restClient.get()
                    .uri(RESULTS_SERVICE_URL + "/test/" + testId);
            if (bearerToken != null)
                req.headers(h -> h.setBearerAuth(bearerToken));

            var response = req.retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {
                    });

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getCandidateHistory(String candidateId, String bearerToken) {
        try {
            var req = restClient.get().uri(RESULTS_SERVICE_URL + "/candidate/" + candidateId);
            if (bearerToken != null)
                req.headers(h -> h.setBearerAuth(bearerToken));

            var response = req.retrieve()
                    .body(new ParameterizedTypeReference<com.abhishek.adminService.dto.ApiResponse<List<Map<String, Object>>>>() {
                    });

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public String exportResults(String testId, String bearerToken) {
        try {
            var req = restClient.get().uri(RESULTS_SERVICE_URL + "/export?testId=" + testId);
            if (bearerToken != null)
                req.headers(h -> h.setBearerAuth(bearerToken));
            return req.retrieve().body(String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
