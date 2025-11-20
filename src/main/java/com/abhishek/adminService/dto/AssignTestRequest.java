package com.abhishek.adminService.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignTestRequest {
    @NotEmpty
    private List<String> candidateIds;
}
