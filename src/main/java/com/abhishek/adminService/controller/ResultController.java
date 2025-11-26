package com.abhishek.adminService.controller;

import com.abhishek.adminService.client.ResultsClient;
import com.abhishek.adminService.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultsClient resultsClient;

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTestResults(@PathVariable String testId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        String token = extractToken(bearerToken);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .message("Test results fetched successfully")
                .data(resultsClient.getTestResults(testId, token))
                .build());
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCandidateHistory(@PathVariable String candidateId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        String token = extractToken(bearerToken);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .message("Candidate history fetched successfully")
                .data(resultsClient.getCandidateHistory(candidateId, token))
                .build());
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> exportResults(@RequestParam String testId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        String token = extractToken(bearerToken);
        String csv = resultsClient.exportResults(testId, token);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=results.csv")
                .body(csv);
    }

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
