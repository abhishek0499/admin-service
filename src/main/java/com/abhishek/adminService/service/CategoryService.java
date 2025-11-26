package com.abhishek.adminService.service;

import com.abhishek.adminService.dto.CreateCategoryRequest;
import com.abhishek.adminService.exception.CategoryNotFoundException;
import com.abhishek.adminService.model.Category;
import com.abhishek.adminService.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequest request) {
        log.info("Creating category: {}", request.getName());

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);
        log.debug("Category created successfully with ID: {}", savedCategory.getId());

        return savedCategory;
    }

    public List<Category> findAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        log.debug("Found {} categories", categories.size());
        return categories;
    }

    public Category updateCategory(String id, CreateCategoryRequest request) {
        log.info("Updating category: {}", id);

        if (!categoryRepository.existsById(id)) {
            log.error("Category not found for update: {}", id);
            throw new CategoryNotFoundException(id);
        }

        Category category = new Category();
        category.setId(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        log.debug("Category updated successfully: {}", id);

        return updatedCategory;
    }

    public void deleteCategory(String id) {
        log.info("Deleting category: {}", id);

        if (!categoryRepository.existsById(id)) {
            log.error("Category not found for deletion: {}", id);
            throw new CategoryNotFoundException(id);
        }

        categoryRepository.deleteById(id);
        log.debug("Category deleted successfully: {}", id);
    }
}