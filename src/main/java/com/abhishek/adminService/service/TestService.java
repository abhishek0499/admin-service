package com.abhishek.adminService.service;

import com.abhishek.adminService.client.AuthClient;
import com.abhishek.adminService.dto.CreateTestRequest;
import com.abhishek.adminService.dto.UserDTO;
import com.abhishek.adminService.dto.event.TestAssignedEvent;
import com.abhishek.adminService.dto.event.TestScheduledEvent;
import com.abhishek.adminService.exception.TestNotFoundException;
import com.abhishek.adminService.model.Test;
import com.abhishek.adminService.repository.TestRepository;
import com.abhishek.adminService.service.publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static com.abhishek.adminService.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthClient authClient;

    private final TaskScheduler taskScheduler = new ConcurrentTaskScheduler();
    private final Map<String, ScheduledFuture<?>> startTasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> endTasks = new HashMap<>();

    public Test createTest(CreateTestRequest testRequest) {
        log.info("Creating test: {}", testRequest.getName());

        Test test = new Test();
        test.setName(testRequest.getName());
        test.setDescription(testRequest.getDescription());
        test.setCategoryIds(testRequest.getCategoryIds());
        test.setQuestionIds(testRequest.getQuestionIds());
        test.setDurationMinutes(testRequest.getDurationMinutes());
        test.setStartAt(testRequest.getStartAt());
        test.setEndAt(testRequest.getEndAt());
        test.setScheduled(false);
        test.setActive(false);

        Test savedTest = testRepository.save(test);
        log.info("Test created successfully with ID: {}", savedTest.getId());

        if (savedTest.getStartAt() != null) {
            log.debug("Scheduling test: {}", savedTest.getId());
            return schedule(savedTest.getId());
        }

        return savedTest;
    }

    public Optional<Test> findById(String testId) {
        log.debug("Finding test by ID: {}", testId);
        return testRepository.findById(testId);
    }

    public List<Test> findAll() {
        log.info("Fetching all tests");
        List<Test> tests = testRepository.findAll();
        log.debug("Found {} tests", tests.size());
        return tests;
    }

    public Test updateTest(String testId, CreateTestRequest testRequest) {
        log.info("Updating test: {}", testId);

        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.error("Test not found for update: {}", testId);
                    return new TestNotFoundException(testId);
                });

        existingTest.setName(testRequest.getName());
        existingTest.setDescription(testRequest.getDescription());
        existingTest.setCategoryIds(testRequest.getCategoryIds());
        existingTest.setQuestionIds(testRequest.getQuestionIds());
        existingTest.setDurationMinutes(testRequest.getDurationMinutes());
        existingTest.setStartAt(testRequest.getStartAt());
        existingTest.setEndAt(testRequest.getEndAt());

        Test updatedTest = testRepository.save(existingTest);
        log.debug("Test updated successfully: {}", testId);

        if (updatedTest.getStartAt() != null) {
            log.debug("Rescheduling test: {}", testId);
            return schedule(updatedTest.getId());
        }

        return updatedTest;
    }

    public void delete(String testId) {
        log.info("Deleting test: {}", testId);

        if (!testRepository.existsById(testId)) {
            log.error("Test not found for deletion: {}", testId);
            throw new TestNotFoundException(testId);
        }

        testRepository.deleteById(testId);
        cancelSchedules(testId);
        log.info("Test deleted successfully: {}", testId);
    }

    public Test schedule(String testId) {
        log.info("Scheduling test: {}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.error("Test not found for scheduling: {}", testId);
                    return new TestNotFoundException(testId);
                });

        if (test.getStartAt() == null) {
            log.error("Cannot schedule test without start time: {}", testId);
            throw new IllegalArgumentException(ERROR_START_TIME_REQUIRED);
        }

        // cancel existing schedules
        cancelSchedules(testId);

        // schedule start
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDateTime currentDateTime = LocalDateTime.now(zoneId);

        if (test.getStartAt().isAfter(currentDateTime)) {
            Instant startTime = test.getStartAt().atZone(zoneId).toInstant();
            ScheduledFuture<?> startFuture = taskScheduler.schedule(() -> startTest(test.getId()), startTime);
            startTasks.put(test.getId(), startFuture);
            log.debug("Test start scheduled for: {}", test.getStartAt());

            // Send scheduled notification
            TestScheduledEvent event = new TestScheduledEvent();
            event.setTestId(test.getId());
            event.setTestName(test.getName());
            event.setScheduledAt(test.getStartAt());
            event.setDuration(test.getDurationMinutes());
            notificationPublisher.publishTestScheduledEvent(event);

        } else {
            // start immediately
            log.debug("Test start time is in the past, starting immediately");
            test.setActive(true);
        }

        // schedule end if present
        if (test.getEndAt() != null) {
            if (test.getEndAt().isAfter(currentDateTime)) {
                Instant endTime = test.getEndAt().atZone(zoneId).toInstant();
                ScheduledFuture<?> endFuture = taskScheduler.schedule(() -> endTest(test.getId()), endTime);
                endTasks.put(test.getId(), endFuture);
                log.debug("Test end scheduled for: {}", test.getEndAt());
            } else {
                log.debug("Test end time is in the past, ending immediately");
                test.setActive(false);
            }
        }

        test.setScheduled(true);
        testRepository.save(test);
        log.info("Test scheduled successfully: {}", testId);
        return test;
    }

    private void startTest(String testId) {
        log.info("Starting test: {}", testId);

        testRepository.findById(testId).ifPresent(test -> {
            test.setActive(true);
            testRepository.save(test);
            log.info("Test activated: {}", testId);
        });
    }

    private void endTest(String testId) {
        log.info("Ending test: {}", testId);

        testRepository.findById(testId).ifPresent(test -> {
            test.setActive(false);
            testRepository.save(test);
            log.info("Test deactivated: {}", testId);
        });
    }

    public Test assignCandidates(String testId, List<String> candidateIds, String bearerToken) {
        log.info("Assigning {} candidates to test: {}", candidateIds.size(), testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> {
                    log.error("Test not found for candidate assignment: {}", testId);
                    return new TestNotFoundException(testId);
                });

        List<String> existingCandidates = test.getAssignedCandidates() == null
                ? new ArrayList<>()
                : new ArrayList<>(test.getAssignedCandidates());

        existingCandidates.addAll(candidateIds);

        // dedupe
        test.setAssignedCandidates(new ArrayList<>(new LinkedHashSet<>(existingCandidates)));
        testRepository.save(test);

        log.info("Candidates assigned successfully to test: {}", testId);
        log.debug("Total candidates now assigned: {}", test.getAssignedCandidates().size());

        // Fetch user details and send notifications
        try {
            Map<String, UserDTO> usersMap = authClient.fetchUsersMap(bearerToken);
            List<TestAssignedEvent.CandidateInfo> candidateInfos = new ArrayList<>();

            for (String candidateId : candidateIds) {
                UserDTO user = usersMap.get(candidateId);
                if (user != null) {
                    TestAssignedEvent.CandidateInfo info = new TestAssignedEvent.CandidateInfo();
                    info.setId(user.getId());
                    info.setName(user.getName());
                    info.setEmail(user.getEmail());
                    candidateInfos.add(info);
                }
            }

            if (!candidateInfos.isEmpty()) {
                TestAssignedEvent event = new TestAssignedEvent();
                event.setTestId(test.getId());
                event.setTestName(test.getName());
                event.setStartTime(test.getStartAt());
                event.setEndTime(test.getEndAt());
                event.setDurationMinutes(test.getDurationMinutes());
                event.setTestLink("http://localhost:3000/test/" + test.getId()); // For testing purpose
                event.setCandidates(candidateInfos);

                notificationPublisher.publishTestAssignedEvent(event);
            }
        } catch (Exception e) {
            log.error("Failed to send assignment notifications", e);
        }

        return test;
    }

    private void cancelSchedules(String testId) {
        log.debug("Cancelling schedules for test: {}", testId);

        ScheduledFuture<?> startTask = startTasks.remove(testId);
        if (startTask != null) {
            startTask.cancel(false);
            log.debug("Start task cancelled for test: {}", testId);
        }

        ScheduledFuture<?> endTask = endTasks.remove(testId);
        if (endTask != null) {
            endTask.cancel(false);
            log.debug("End task cancelled for test: {}", testId);
        }
    }

    public List<Test> getTestsForCandidate(String candidateId) {
        log.info("Fetching tests for candidate: {}", candidateId);
        List<Test> tests = testRepository.findByAssignedCandidatesContains(candidateId);
        log.debug("Found {} tests for candidate: {}", tests.size(), candidateId);
        return tests;
    }
}
