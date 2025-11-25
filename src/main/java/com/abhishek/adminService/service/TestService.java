package com.abhishek.adminService.service;

import com.abhishek.adminService.dto.CreateTestRequest;
import com.abhishek.adminService.model.Test;
import com.abhishek.adminService.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final NotificationClient notificationClient;

    // Simple in-memory scheduled task registry (for POC). For production use Quartz
    // or persistent scheduler.
    private final TaskScheduler taskScheduler = new ConcurrentTaskScheduler();
    private final Map<String, ScheduledFuture<?>> startTasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> endTasks = new HashMap<>();

    public Test createTest(CreateTestRequest testRequest) {
        log.debug("createTest");
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
        if (savedTest.getStartAt() != null) {
            return schedule(savedTest.getId());
        }
        log.debug("Test saved with id {}", savedTest.getId());
        return savedTest;
    }

    public Optional<Test> findById(String testId) {
        log.debug("findById with testId: {}", testId);
        return testRepository.findById(testId);
    }

    public List<Test> findAll() {
        return testRepository.findAll();
    }

    public Test updateTest(String testId, CreateTestRequest testRequest) {
        log.debug("updateTest with testId: {}", testId);
        Test existingTest = testRepository.findById(testId)
                .orElseThrow(() -> new NoSuchElementException("Test not found"));

        existingTest.setName(testRequest.getName());
        existingTest.setDescription(testRequest.getDescription());
        existingTest.setCategoryIds(testRequest.getCategoryIds());
        existingTest.setQuestionIds(testRequest.getQuestionIds());
        existingTest.setDurationMinutes(testRequest.getDurationMinutes());
        existingTest.setStartAt(testRequest.getStartAt());
        existingTest.setEndAt(testRequest.getEndAt());

        Test updatedTest = testRepository.save(existingTest);
        if (updatedTest.getStartAt() != null) {
            return schedule(updatedTest.getId());
        }
        return updatedTest;
    }

    public void delete(String testId) {
        testRepository.deleteById(testId);
        cancelSchedules(testId);
    }

    public Test schedule(String testId) {
        log.debug("schedule with testId: {}", testId);
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NoSuchElementException("Test not found"));
        if (test.getStartAt() == null) {
            throw new IllegalArgumentException("startAt required to schedule");
        }

        // cancel existing schedules
        cancelSchedules(testId);

        // schedule start - using UTC timezone for comparison
        ZoneId zoneId = ZoneId.of("UTC");
        LocalDateTime now = LocalDateTime.now(zoneId); // Get current time in UTC

        if (test.getStartAt().isAfter(now)) {
            Date startTime = Date.from(test.getStartAt().atZone(zoneId).toInstant());
            ScheduledFuture<?> f = taskScheduler.schedule(() -> startTest(test.getId()), startTime);
            startTasks.put(test.getId(), f);
        } else {
            // start immediately
            startTest(test.getId());
        }

        // schedule end if present
        if (test.getEndAt() != null) {
            if (test.getEndAt().isAfter(now)) {
                Date endTime = Date.from(test.getEndAt().atZone(zoneId).toInstant());
                ScheduledFuture<?> f2 = taskScheduler.schedule(() -> endTest(test.getId()), endTime);
                endTasks.put(test.getId(), f2);
            } else {
                endTest(test.getId());
            }
        }

        test.setScheduled(true);
        testRepository.save(test);
        return test;
    }

    private void startTest(String testId) {
        testRepository.findById(testId).ifPresent(test -> {
            test.setActive(true);
            testRepository.save(test);
            // send notifications to assigned candidates
            if (test.getAssignedCandidates() != null && !test.getAssignedCandidates().isEmpty()) {
                notificationClient.notifyTestStarted(test);
            }
        });
    }

    private void endTest(String testId) {
        testRepository.findById(testId).ifPresent(test -> {
            test.setActive(false);
            testRepository.save(test);
            if (test.getAssignedCandidates() != null && !test.getAssignedCandidates().isEmpty()) {
                notificationClient.notifyTestEnded(test);
            }
        });
    }

    public Test assignCandidates(String testId, List<String> candidateIds) {
        log.debug("assignCandidates with testId: {}", testId);
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NoSuchElementException("Test not found"));
        List<String> list = test.getAssignedCandidates() == null ? new ArrayList<>()
                : new ArrayList<>(test.getAssignedCandidates());
        list.addAll(candidateIds);
        // dedupe
        test.setAssignedCandidates(new ArrayList<>(new LinkedHashSet<>(list)));
        testRepository.save(test);

        // optionally notify candidates immediately about assignment
        notificationClient.notifyAssigned(test, candidateIds);
        return test;
    }

    private void cancelSchedules(String testId) {
        ScheduledFuture<?> startTask = startTasks.remove(testId);
        if (startTask != null)
            startTask.cancel(false);
        ScheduledFuture<?> endTask = endTasks.remove(testId);
        if (endTask != null)
            endTask.cancel(false);
    }

    public List<Test> getTestsForCandidate(String candidateId) {
        return testRepository.findByAssignedCandidatesContains(candidateId);
    }
}
