package com.abhishek.adminService.service;

import com.abhishek.adminService.model.Category;
import com.abhishek.adminService.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public Category create(Category c) {
        return categoryRepository.save(c);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category update(String id, Category c) {
        c.setId(id);
        return categoryRepository.save(c);
    }

    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}