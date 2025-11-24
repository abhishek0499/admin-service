package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.AssignTestRequest;
import com.abhishek.adminService.dto.CreateTestRequest;
import com.abhishek.adminService.model.TestDef;
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TestDef> create(@Valid @RequestBody CreateTestRequest req) {
        TestDef t = new TestDef();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setCategoryIds(req.getCategoryIds());
        t.setQuestionIds(req.getQuestionIds());
        t.setDurationMinutes(req.getDurationMinutes());
        t.setStartAt(req.getStartAt());
        t.setEndAt(req.getEndAt());
        return ResponseEntity.ok(testService.create(t));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TestDef>> list() {
        return ResponseEntity.ok(testService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'CANDIDATE')")
    public ResponseEntity<TestDef> getById(@PathVariable String id) {
        return ResponseEntity.ok(testService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Test not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TestDef> update(@PathVariable String id, @Valid @RequestBody CreateTestRequest req) {
        TestDef t = new TestDef();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setCategoryIds(req.getCategoryIds());
        t.setQuestionIds(req.getQuestionIds());
        t.setDurationMinutes(req.getDurationMinutes());
        t.setStartAt(req.getStartAt());
        t.setEndAt(req.getEndAt());
        return ResponseEntity.ok(testService.update(id, t));
    }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TestDef> schedule(@PathVariable String id) {
        return ResponseEntity.ok(testService.schedule(id));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TestDef> assign(@PathVariable String id, @Valid @RequestBody AssignTestRequest req) {
        return ResponseEntity.ok(testService.assignCandidates(id, req.getCandidateIds()));
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'CANDIDATE')")
    public ResponseEntity<List<TestDef>> getTestsForCandidate(@PathVariable String candidateId) {
        return ResponseEntity.ok(testService.getTestsForCandidate(candidateId));
    }
}