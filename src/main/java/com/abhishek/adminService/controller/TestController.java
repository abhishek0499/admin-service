package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.AssignTestRequest;
import com.abhishek.adminService.dto.CreateTestRequest;
import com.abhishek.adminService.model.Test;
import com.abhishek.adminService.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/admin/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> createTest(@Valid @RequestBody CreateTestRequest testRequest) {
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message("Test created successfully")
                .data(testService.createTest(testRequest))
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Test>>> getAllTests() {
        return ResponseEntity.ok(ApiResponse.<List<Test>>builder()
                .message("Tests fetched successfully")
                .data(testService.findAll())
                .build());
    }

    @GetMapping("/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<Test>> getTestById(@PathVariable String testId) {
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message("Test fetched successfully")
                .data(testService.findById(testId)
                        .orElseThrow(() -> new NoSuchElementException("Test not found")))
                .build());
    }

    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> updateTest(@PathVariable String testId,
                                                        @Valid @RequestBody CreateTestRequest testRequest) {
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message("Test updated successfully")
                .data(testService.updateTest(testId, testRequest))
                .build());
    }

    @PostMapping("/{testId}/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> scheduleTest(@PathVariable String testId) {
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message("Test scheduled successfully")
                .data(testService.schedule(testId))
                .build());
    }

    @PostMapping("/{testId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> assignTest(@PathVariable String testId,
                                                        @Valid @RequestBody AssignTestRequest assignRequest) {
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message("Test assigned successfully")
                .data(testService.assignCandidates(testId, assignRequest.getCandidateIds()))
                .build());
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<List<Test>>> getTestsForCandidate(@PathVariable String candidateId) {
        return ResponseEntity.ok(ApiResponse.<List<Test>>builder()
                .message("Candidate tests fetched successfully")
                .data(testService.getTestsForCandidate(candidateId))
                .build());
    }
}