package com.abhishek.adminService.repository;

import com.abhishek.adminService.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CategoryRepository extends MongoRepository<Category, String> {}