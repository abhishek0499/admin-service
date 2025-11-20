package com.abhishek.adminService.service;

import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question create(Question q, String createdBy) {
        q.setCreatedAt(Instant.now());
        q.setCreatedBy(createdBy);
        return questionRepository.save(q);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public List<Question> findByCategory(String categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public Question update(String id, Question updated) {
        updated.setId(id);
        return questionRepository.save(updated);
    }

    public void delete(String id) {
        questionRepository.deleteById(id);
    }
}