package com.abhishek.adminService.exception;

/**
 * Custom exception for category not found errors
 */
public class CategoryNotFoundException extends RuntimeException {

    private final String categoryId;

    public CategoryNotFoundException(String categoryId) {
        super(String.format("Category not found with ID: %s", categoryId));
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
