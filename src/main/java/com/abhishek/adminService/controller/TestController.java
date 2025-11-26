package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.AssignTestRequest;
import com.abhishek.adminService.dto.CreateTestRequest;
import com.abhishek.adminService.exception.TestNotFoundException;
import com.abhishek.adminService.model.Test;
import com.abhishek.adminService.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.abhishek.adminService.constant.Constants.*;

@Slf4j
@RestController
@RequestMapping(ENDPOINT_ADMIN + ENDPOINT_TESTS)
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> createTest(@Valid @RequestBody CreateTestRequest testRequest) {
        log.info("POST {} - Creating test: {}", ENDPOINT_ADMIN + ENDPOINT_TESTS, testRequest.getName());

        Test createdTest = testService.createTest(testRequest);

        log.debug("Test created successfully with ID: {}", createdTest.getId());
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message(MSG_TEST_CREATED)
                .data(createdTest)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Test>>> getAllTests() {
        log.info("GET {} - Fetching all tests", ENDPOINT_ADMIN + ENDPOINT_TESTS);

        List<Test> tests = testService.findAll();

        log.debug("Returning {} tests", tests.size());
        return ResponseEntity.ok(ApiResponse.<List<Test>>builder()
                .message(MSG_TESTS_FETCHED)
                .data(tests)
                .build());
    }

    @GetMapping("/{testId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<Test>> getTestById(@PathVariable String testId) {
        log.info("GET {}/{} - Fetching test by ID", ENDPOINT_ADMIN + ENDPOINT_TESTS, testId);

        Test test = testService.findById(testId)
                .orElseThrow(() -> {
                    log.error("Test not found: {}", testId);
                    return new TestNotFoundException(testId);
                });

        log.debug("Test fetched successfully: {}", testId);
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message(MSG_TESTS_FETCHED)
                .data(test)
                .build());
    }

    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> updateTest(@PathVariable String testId,
                                                        @Valid @RequestBody CreateTestRequest testRequest) {
        log.info("PUT {}/{} - Updating test", ENDPOINT_ADMIN + ENDPOINT_TESTS, testId);

        Test updatedTest = testService.updateTest(testId, testRequest);

        log.debug("Test updated successfully: {}", testId);
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message(MSG_TEST_UPDATED)
                .data(updatedTest)
                .build());
    }

    @PostMapping("/{testId}" + ENDPOINT_SCHEDULE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> scheduleTest(@PathVariable String testId) {
        log.info("POST {}/{}{} - Scheduling test", ENDPOINT_ADMIN + ENDPOINT_TESTS, testId, ENDPOINT_SCHEDULE);

        Test scheduledTest = testService.schedule(testId);

        log.debug("Test scheduled successfully: {}", testId);
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message(MSG_TEST_SCHEDULED)
                .data(scheduledTest)
                .build());
    }

    @PostMapping("/{testId}" + ENDPOINT_ASSIGN)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Test>> assignTest(@PathVariable String testId,
                                                        @Valid @RequestBody AssignTestRequest assignRequest,
                                                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.info("POST {}/{}{} - Assigning {} candidates to test",
                ENDPOINT_ADMIN + ENDPOINT_TESTS, testId, ENDPOINT_ASSIGN,
                assignRequest.getCandidateIds().size());

        String bearerToken = extractBearerToken(authorizationHeader);
        Test assignedTest = testService.assignCandidates(testId, assignRequest.getCandidateIds(), bearerToken);

        log.debug("Candidates assigned successfully to test: {}", testId);
        return ResponseEntity.ok(ApiResponse.<Test>builder()
                .message(MSG_CANDIDATES_ASSIGNED)
                .data(assignedTest)
                .build());
    }

    @GetMapping(ENDPOINT_CANDIDATE + "/{candidateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<List<Test>>> getTestsForCandidate(@PathVariable String candidateId) {
        log.info("GET {}{}/{} - Fetching tests for candidate",
                ENDPOINT_ADMIN + ENDPOINT_TESTS, ENDPOINT_CANDIDATE, candidateId);

        List<Test> tests = testService.getTestsForCandidate(candidateId);

        log.debug("Returning {} tests for candidate: {}", tests.size(), candidateId);
        return ResponseEntity.ok(ApiResponse.<List<Test>>builder()
                .message(MSG_TESTS_FETCHED)
                .data(tests)
                .build());
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}