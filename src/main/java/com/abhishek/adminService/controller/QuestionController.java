package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.CreateQuestionRequest;
import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Question> create(@Valid @RequestBody CreateQuestionRequest req,
            @RequestHeader(value = "X-User-Id", required = false) String createdBy) {
        Question q = new Question();
        q.setCategoryId(req.getCategoryId());
        q.setDifficulty(req.getDifficulty());
        q.setText(req.getText());
        q.setOptions(req.getOptions().stream().map(o -> {
            Question.Option opt = new Question.Option();
            opt.setId(o.getId());
            opt.setText(o.getText());
            return opt;
        }).toList());
        q.setCorrectOptionId(req.getCorrectOptionId());
        Question saved = questionService.create(q, createdBy == null ? "system" : createdBy);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'CANDIDATE')")
    public ResponseEntity<List<Question>> list(@RequestParam(required = false) String categoryId) {
        List<Question> res = categoryId == null ? questionService.findAll()
                : questionService.findByCategory(categoryId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'CANDIDATE')")
    public ResponseEntity<List<Question>> bulk(@RequestBody List<String> ids) {
        return ResponseEntity.ok(questionService.findAllById(ids));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Question> update(@PathVariable String id, @Valid @RequestBody CreateQuestionRequest req) {
        Question q = new Question();
        q.setCategoryId(req.getCategoryId());
        q.setDifficulty(req.getDifficulty());
        q.setText(req.getText());
        q.setOptions(req.getOptions().stream().map(o -> {
            Question.Option opt = new Question.Option();
            opt.setId(o.getId());
            opt.setText(o.getText());
            return opt;
        }).toList());
        q.setCorrectOptionId(req.getCorrectOptionId());
        Question updated = questionService.update(id, q);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}