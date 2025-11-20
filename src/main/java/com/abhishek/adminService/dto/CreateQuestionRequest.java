package com.abhishek.adminService.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {
    private String categoryId;
    private String difficulty;
    private String text;
    private List<OptionDto> options;
    private String correctOptionId;


    @Data
    public static class OptionDto {
        private String id;
        private String text;
    }
}




