package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.CreateCategoryRequest;
import com.abhishek.adminService.model.Category;
import com.abhishek.adminService.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CreateCategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.<Category>builder()
                .message("Category created successfully")
                .data(categoryService.createCategory(req))
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Category>>> listCategory() {
        return ResponseEntity.ok(ApiResponse.<List<Category>>builder()
                .message("Categories fetched successfully")
                .data(categoryService.findAllCategories())
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable String id,
            @Valid @RequestBody CreateCategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.<Category>builder()
                .message("Category updated successfully")
                .data(categoryService.updateCategory(id, req))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Category deleted successfully")
                .build());
    }
}