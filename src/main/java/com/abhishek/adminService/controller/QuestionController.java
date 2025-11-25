package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.CreateQuestionRequest;
import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Question>> createQuestion(@Valid @RequestBody CreateQuestionRequest questionRequest,
                                                                HttpServletRequest servletRequest) {
        Question saved = questionService.createQuestion(questionRequest,servletRequest);
        return ResponseEntity.ok(ApiResponse.<Question>builder()
                .message("Question created successfully")
                .data(saved)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<List<Question>>> getQuestionByCategoryId(@RequestParam(required = false) String categoryId) {
        List<Question> res = categoryId == null ? questionService.findAllQuestions()
                : questionService.findByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.<List<Question>>builder()
                .message("Questions fetched successfully")
                .data(res)
                .build());
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ResponseEntity<ApiResponse<List<Question>>> getAllQuestionsById(@RequestBody List<String> ids) {
        return ResponseEntity.ok(ApiResponse.<List<Question>>builder()
                .message("Bulk questions fetched successfully")
                .data(questionService.findAllQuestionsById(ids))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Question>> updateQuestion(@PathVariable String id,
                                                                @Valid @RequestBody CreateQuestionRequest questionRequest) {
        Question updated = questionService.updateQuestion(id, questionRequest);
        return ResponseEntity.ok(ApiResponse.<Question>builder()
                .message("Question updated successfully")
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .build());
    }
}