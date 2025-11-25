package com.abhishek.adminService.constant;

/**
 * Centralized constants for the Admin Service
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // ==================== Success Messages ====================
    public static final String MSG_TEST_CREATED = "Test created successfully";
    public static final String MSG_TEST_UPDATED = "Test updated successfully";
    public static final String MSG_TEST_DELETED = "Test deleted successfully";
    public static final String MSG_TEST_SCHEDULED = "Test scheduled successfully";
    public static final String MSG_CANDIDATES_ASSIGNED = "Candidates assigned successfully";

    public static final String MSG_CATEGORY_CREATED = "Category created successfully";
    public static final String MSG_CATEGORY_UPDATED = "Category updated successfully";
    public static final String MSG_CATEGORY_DELETED = "Category deleted successfully";

    public static final String MSG_QUESTION_CREATED = "Question created successfully";
    public static final String MSG_QUESTION_UPDATED = "Question updated successfully";
    public static final String MSG_QUESTION_DELETED = "Question deleted successfully";

    public static final String MSG_TESTS_FETCHED = "Tests fetched successfully";
    public static final String MSG_CATEGORIES_FETCHED = "Categories fetched successfully";
    public static final String MSG_QUESTIONS_FETCHED = "Questions fetched successfully";

    // ==================== Error Messages ====================
    public static final String ERROR_TEST_NOT_FOUND = "Test not found";
    public static final String ERROR_CATEGORY_NOT_FOUND = "Category not found";
    public static final String ERROR_QUESTION_NOT_FOUND = "Question not found";
    public static final String ERROR_START_TIME_REQUIRED = "Start time is required to schedule a test";
    public static final String ERROR_VALIDATION_FAILED = "Validation failed";

    // ==================== Endpoint Paths ====================
    public static final String ENDPOINT_ADMIN = "/admin";
    public static final String ENDPOINT_TESTS = "/tests";
    public static final String ENDPOINT_CATEGORIES = "/categories";
    public static final String ENDPOINT_QUESTIONS = "/questions";
    public static final String ENDPOINT_SCHEDULE = "/schedule";
    public static final String ENDPOINT_ASSIGN = "/assign";
    public static final String ENDPOINT_CANDIDATE = "/candidate";
}
