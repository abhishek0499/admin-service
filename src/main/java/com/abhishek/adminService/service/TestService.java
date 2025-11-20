package com.abhishek.adminService.service;

import com.abhishek.adminService.model.TestDef;
import com.abhishek.adminService.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final NotificationClient notificationClient; // stub client to send notifications

    // Simple in-memory scheduled task registry (for POC). For production use Quartz or persistent scheduler.
    private final TaskScheduler taskScheduler = new ConcurrentTaskScheduler();
    private final Map<String, ScheduledFuture<?>> startTasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> endTasks = new HashMap<>();


    public TestDef create(TestDef t) {
        t.setScheduled(false);
        t.setActive(false);
        return testRepository.save(t);
    }

    public Optional<TestDef> findById(String id) {
        return testRepository.findById(id);
    }

    public List<TestDef> findAll() {
        return testRepository.findAll();
    }

    public TestDef update(String id, TestDef updated) {
        updated.setId(id);
        return testRepository.save(updated);
    }

    public void delete(String id) {
        testRepository.deleteById(id);
        cancelSchedules(id);
    }

    public TestDef schedule(String id) {
        TestDef t = testRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Test not found"));
        if (t.getStartAt() == null) {
            throw new IllegalArgumentException("startAt required to schedule");
        }

        // cancel existing schedules
        cancelSchedules(id);

        // schedule start
        Instant now = Instant.now();
        if (t.getStartAt().isAfter(now)) {
            long delayMs = t.getStartAt().toEpochMilli() - now.toEpochMilli();
            ScheduledFuture<?> f = taskScheduler.schedule(() ->
                    startTest(t.getId()), Date.from(t.getStartAt()));
            startTasks.put(t.getId(), f);
        } else {
        // start immediately
            startTest(t.getId());
        }

        // schedule end if present
        if (t.getEndAt() != null) {
            if (t.getEndAt().isAfter(now)) {
                ScheduledFuture<?> f2 = taskScheduler.schedule(() ->
                        endTest(t.getId()), Date.from(t.getEndAt()));
                endTasks.put(t.getId(), f2);
            } else {
                endTest(t.getId());
            }
        }

        t.setScheduled(true);
        testRepository.save(t);
        return t;
    }


    private void startTest(String id) {
        testRepository.findById(id).ifPresent(t -> {
            t.setActive(true);
            testRepository.save(t);
            // send notifications to assigned candidates
            if (t.getAssignedCandidates() != null && !t.getAssignedCandidates().isEmpty()) {
                notificationClient.notifyTestStarted(t);
            }
        });
    }

    private void endTest(String id) {
        testRepository.findById(id).ifPresent(t -> {
            t.setActive(false);
            testRepository.save(t);
            if (t.getAssignedCandidates() != null && !t.getAssignedCandidates().isEmpty()) {
                notificationClient.notifyTestEnded(t);
            }
        });
    }


    public TestDef assignCandidates(String id, List<String> candidateIds) {
        TestDef t = testRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Test not found"));
        List<String> list = t.getAssignedCandidates() == null ? new ArrayList<>() : new ArrayList<>(t.getAssignedCandidates());
        list.addAll(candidateIds);
        // dedupe
        t.setAssignedCandidates(new ArrayList<>(new LinkedHashSet<>(list)));
        testRepository.save(t);

        // optionally notify candidates immediately about assignment
        notificationClient.notifyAssigned(t, candidateIds);
        return t;
    }


    private void cancelSchedules(String id) {
        ScheduledFuture<?> s = startTasks.remove(id);
        if (s != null) s.cancel(false);
        ScheduledFuture<?> e = endTasks.remove(id);
        if (e != null) e.cancel(false);
    }

}
