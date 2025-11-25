package com.abhishek.adminService.controller;

import com.abhishek.adminService.dto.ApiResponse;
import com.abhishek.adminService.dto.CreateCategoryRequest;
import com.abhishek.adminService.model.Category;
import com.abhishek.adminService.service.CategoryService;
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
@RequestMapping(ENDPOINT_ADMIN + ENDPOINT_CATEGORIES)
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("POST {} - Creating category: {}", ENDPOINT_ADMIN + ENDPOINT_CATEGORIES, request.getName());

        Category createdCategory = categoryService.createCategory(request);

        log.debug("Category created successfully with ID: {}", createdCategory.getId());
        return ResponseEntity.ok(ApiResponse.<Category>builder()
                .message(MSG_CATEGORY_CREATED)
                .data(createdCategory)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Category>>> listCategory() {
        log.info("GET {} - Fetching all categories", ENDPOINT_ADMIN + ENDPOINT_CATEGORIES);

        List<Category> categories = categoryService.findAllCategories();

        log.debug("Returning {} categories", categories.size());
        return ResponseEntity.ok(ApiResponse.<List<Category>>builder()
                .message(MSG_CATEGORIES_FETCHED)
                .data(categories)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable String id,
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("PUT {}/{} - Updating category", ENDPOINT_ADMIN + ENDPOINT_CATEGORIES, id);

        Category updatedCategory = categoryService.updateCategory(id, request);

        log.debug("Category updated successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.<Category>builder()
                .message(MSG_CATEGORY_UPDATED)
                .data(updatedCategory)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        log.info("DELETE {}/{} - Deleting category", ENDPOINT_ADMIN + ENDPOINT_CATEGORIES, id);

        categoryService.deleteCategory(id);

        log.debug("Category deleted successfully: {}", id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message(MSG_CATEGORY_DELETED)
                .build());
    }
}