package com.abhishek.adminService.exception;

import com.abhishek.adminService.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.abhishek.adminService.constant.Constants.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception) {

        log.debug("Validation exception details:", exception);

        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
            log.debug("Validation error - Field: {}, Message: {}", fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .message(ERROR_VALIDATION_FAILED)
                        .data(validationErrors)
                        .build());
    }

    /**
     * Handle TestNotFoundException
     */
    @ExceptionHandler(TestNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleTestNotFound(TestNotFoundException exception) {
        log.error("Test not found - ID: {}", exception.getTestId());
        log.debug("TestNotFoundException details:", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Handle CategoryNotFoundException
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCategoryNotFound(CategoryNotFoundException exception) {
        log.error("Category not found - ID: {}", exception.getCategoryId());
        log.debug("CategoryNotFoundException details:", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Handle QuestionNotFoundException
     */
    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuestionNotFound(QuestionNotFoundException exception) {
        log.error("Question not found - ID: {}", exception.getQuestionId());
        log.debug("QuestionNotFoundException details:", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Handle NoSuchElementException (fallback for generic not found)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElement(NoSuchElementException exception) {
        log.error("Resource not found: {}", exception.getMessage());
        log.debug("NoSuchElementException details:", exception);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        log.error("Illegal argument: {}", exception.getMessage());
        log.debug("IllegalArgumentException stack trace:", exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Handle all other uncaught exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception exception) {
        log.error("Unhandled exception occurred: {}", exception.getMessage());
        log.error("Exception stack trace:", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .message("An unexpected error occurred. Please try again later.")
                        .build());
    }
}
