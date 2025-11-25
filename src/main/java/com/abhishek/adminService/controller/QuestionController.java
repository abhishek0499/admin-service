package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.CreateQuestionRequest;
import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping(ENDPOINT_ADMIN + ENDPOINT_QUESTIONS)
@RequiredArgsConstructor
public class QuestionController {
        private final QuestionService questionService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Question>> createQuestion(
                        @Valid @RequestBody CreateQuestionRequest questionRequest,
                        HttpServletRequest servletRequest) {
                log.info("POST {} - Creating question in category: {}",
                                ENDPOINT_ADMIN + ENDPOINT_QUESTIONS, questionRequest.getCategoryId());

                Question savedQuestion = questionService.createQuestion(questionRequest, servletRequest);

                log.debug("Question created successfully with ID: {}", savedQuestion.getId());
                return ResponseEntity.ok(ApiResponse.<Question>builder()
                                .message(MSG_QUESTION_CREATED)
                                .data(savedQuestion)
                                .build());
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
        public ResponseEntity<ApiResponse<List<Question>>> getQuestionByCategoryId(
                        @RequestParam(required = false) String categoryId) {

                if (categoryId == null) {
                        log.info("GET {} - Fetching all questions", ENDPOINT_ADMIN + ENDPOINT_QUESTIONS);
                } else {
                        log.info("GET {} - Fetching questions for category: {}",
                                        ENDPOINT_ADMIN + ENDPOINT_QUESTIONS, categoryId);
                }

                List<Question> questions = categoryId == null
                                ? questionService.findAllQuestions()
                                : questionService.findByCategory(categoryId);

                log.debug("Returning {} questions", questions.size());
                return ResponseEntity.ok(ApiResponse.<List<Question>>builder()
                                .message(MSG_QUESTIONS_FETCHED)
                                .data(questions)
                                .build());
        }

        @PostMapping("/bulk")
        @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
        public ResponseEntity<ApiResponse<List<Question>>> getAllQuestionsById(@RequestBody List<String> ids) {
                log.info("POST {}/bulk - Fetching {} questions by IDs", ENDPOINT_ADMIN + ENDPOINT_QUESTIONS,
                                ids.size());

                List<Question> questions = questionService.findAllQuestionsById(ids);

                log.debug("Returning {} questions out of {} requested", questions.size(), ids.size());
                return ResponseEntity.ok(ApiResponse.<List<Question>>builder()
                                .message(MSG_QUESTIONS_FETCHED)
                                .data(questions)
                                .build());
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Question>> updateQuestion(@PathVariable String id,
                        @Valid @RequestBody CreateQuestionRequest questionRequest) {
                log.info("PUT {}/{} - Updating question", ENDPOINT_ADMIN + ENDPOINT_QUESTIONS, id);

                Question updatedQuestion = questionService.updateQuestion(id, questionRequest);

                log.debug("Question updated successfully: {}", id);
                return ResponseEntity.ok(ApiResponse.<Question>builder()
                                .message(MSG_QUESTION_UPDATED)
                                .data(updatedQuestion)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable String id) {
                log.info("DELETE {}/{} - Deleting question", ENDPOINT_ADMIN + ENDPOINT_QUESTIONS, id);

                questionService.deleteQuestion(id);

                log.debug("Question deleted successfully: {}", id);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .message(MSG_QUESTION_DELETED)
                                .build());
        }
}