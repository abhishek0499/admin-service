package com.abhishek.adminService.exception;

/**
 * Custom exception for question not found errors
 */
public class QuestionNotFoundException extends RuntimeException {

    private final String questionId;

    public QuestionNotFoundException(String questionId) {
        super(String.format("Question not found with ID: %s", questionId));
        this.questionId = questionId;
    }

    public String getQuestionId() {
        return questionId;
    }
}
