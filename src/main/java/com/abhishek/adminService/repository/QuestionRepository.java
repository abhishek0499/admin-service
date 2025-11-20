package com.abhishek.adminService.repository;

import com.abhishek.adminService.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByCategoryId(String categoryId);
}