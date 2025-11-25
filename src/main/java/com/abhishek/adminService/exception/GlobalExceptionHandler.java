package com.abhishek.adminService.exception;

import com.abhishek.adminService.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String msg = err.getDefaultMessage();
            errors.put(field, msg);
        });
        return new ResponseEntity<>(ApiResponse.<Map<String, String>>builder()
                .message("Validation failed")
                .data(errors)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException ex) {
        return new ResponseEntity<>(ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
