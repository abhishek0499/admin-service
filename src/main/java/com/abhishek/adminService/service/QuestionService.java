package com.abhishek.adminService.service;

import com.abhishek.adminService.dto.CreateQuestionRequest;
import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.repository.QuestionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question createQuestion(CreateQuestionRequest questionRequest, HttpServletRequest servletRequest) {
        String createdBy = (String) servletRequest.getAttribute("principalId");
        Question question = new Question();
        question.setCategoryId(questionRequest.getCategoryId());
        question.setDifficulty(questionRequest.getDifficulty());
        question.setText(questionRequest.getText());
        question.setOptions(questionRequest.getOptions().stream().map(optionDto -> {
            Question.Option option = new Question.Option();
            option.setId(optionDto.getId());
            option.setText(optionDto.getText());
            return option;
        }).toList());
        question.setCorrectOptionId(questionRequest.getCorrectOptionId());
        question.setCreatedAt(Instant.now());
        question.setCreatedBy(createdBy);
        return questionRepository.save(question);
    }

    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> findByCategory(String categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public Question updateQuestion(String id, CreateQuestionRequest questionRequest) {
        Question question = new Question();
        question.setId(id);
        question.setCategoryId(questionRequest.getCategoryId());
        question.setDifficulty(questionRequest.getDifficulty());
        question.setText(questionRequest.getText());
        question.setOptions(questionRequest.getOptions().stream().map(optionDto -> {
            Question.Option opt = new Question.Option();
            opt.setId(optionDto.getId());
            opt.setText(optionDto.getText());
            return opt;
        }).toList());
        question.setCorrectOptionId(questionRequest.getCorrectOptionId());
        return questionRepository.save(question);
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    public List<Question> findAllQuestionsById(List<String> ids) {
        return questionRepository.findAllById(ids);
    }
}