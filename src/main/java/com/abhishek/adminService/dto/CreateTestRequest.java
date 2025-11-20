package com.abhishek.adminService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CreateTestRequest {
    @NotBlank
    private String name;
    private String description;
    private List<String> categoryIds;
    private List<String> questionIds;
    @Min(1) private int durationMinutes;
    private Instant startAt;
    private Instant endAt;
}