package com.abhishek.adminService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;
import java.util.List;


@Document(collection = "tests")
@Data
public class TestDef {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> categoryIds; // optional
    private List<String> questionIds; // explicit questions
    private int durationMinutes;
    private Instant startAt;
    private Instant endAt;
    private List<String> assignedCandidates; // userIds
    private boolean scheduled;
    private boolean active;
}