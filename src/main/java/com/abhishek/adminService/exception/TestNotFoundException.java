package com.abhishek.adminService.exception;

/**
 * Custom exception for test not found errors
 */
public class TestNotFoundException extends RuntimeException {

    private final String testId;

    public TestNotFoundException(String testId) {
        super(String.format("Test not found with ID: %s", testId));
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }
}
