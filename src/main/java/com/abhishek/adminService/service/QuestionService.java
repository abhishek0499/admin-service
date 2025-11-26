package com.abhishek.adminService.service;

import com.abhishek.adminService.dto.CreateQuestionRequest;
import com.abhishek.adminService.exception.QuestionNotFoundException;
import com.abhishek.adminService.model.Question;
import com.abhishek.adminService.repository.QuestionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question createQuestion(CreateQuestionRequest questionRequest, HttpServletRequest servletRequest) {
        String createdBy = (String) servletRequest.getAttribute("principalId");
        log.info("Creating question in category: {}, created by: {}", questionRequest.getCategoryId(), createdBy);

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

        Question savedQuestion = questionRepository.save(question);
        log.debug("Question created successfully with ID: {}", savedQuestion.getId());

        return savedQuestion;
    }

    public List<Question> findAllQuestions() {
        log.info("Fetching all questions");
        List<Question> questions = questionRepository.findAll();
        log.debug("Found {} questions", questions.size());
        return questions;
    }

    public List<Question> findByCategory(String categoryId) {
        log.info("Fetching questions for category: {}", categoryId);
        List<Question> questions = questionRepository.findByCategoryId(categoryId);
        log.debug("Found {} questions for category: {}", questions.size(), categoryId);
        return questions;
    }

    public Question updateQuestion(String id, CreateQuestionRequest questionRequest) {
        log.info("Updating question: {}", id);

        if (!questionRepository.existsById(id)) {
            log.error("Question not found for update: {}", id);
            throw new QuestionNotFoundException(id);
        }

        Question question = new Question();
        question.setId(id);
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

        Question updatedQuestion = questionRepository.save(question);
        log.debug("Question updated successfully: {}", id);

        return updatedQuestion;
    }

    public void deleteQuestion(String id) {
        log.info("Deleting question: {}", id);

        if (!questionRepository.existsById(id)) {
            log.error("Question not found for deletion: {}", id);
            throw new QuestionNotFoundException(id);
        }

        questionRepository.deleteById(id);
        log.debug("Question deleted successfully: {}", id);
    }

    public List<Question> findAllQuestionsById(List<String> ids) {
        log.info("Fetching {} questions by IDs", ids.size());
        List<Question> questions = questionRepository.findAllById(ids);
        log.debug("Found {} questions out of {} requested", questions.size(), ids.size());
        return questions;
    }
}