package com.abhishek.adminService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;
import java.util.List;


@Document(collection = "questions")
@Data
public class Question {
    @Id
    private String id;
    private String categoryId;
    private String difficulty; // EASY/MEDIUM/HARD
    private String text;
    private List<Option> options;
    private String correctOptionId; // server-side only
    private Instant createdAt;
    private String createdBy;


    @Data
    public static class Option {
        private String id;
        private String text;
    }
}