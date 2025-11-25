package com.abhishek.adminService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tests")
@Data
public class Test {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> categoryIds; // optional
    private List<String> questionIds; // explicit questions
    private int durationMinutes;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<String> assignedCandidates; // userIds
    private boolean scheduled;
    private boolean active;
}