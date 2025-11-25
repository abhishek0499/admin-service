package com.abhishek.adminService.service;

import com.abhishek.adminService.dto.CreateCategoryRequest;
import com.abhishek.adminService.model.Category;
import com.abhishek.adminService.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(String id, CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setId(id);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}