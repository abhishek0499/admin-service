package com.abhishek.adminService.dto.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestAssignedEvent implements Serializable {
    private String eventType = "TEST_ASSIGNED";
    private String testId;
    private String testName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String testLink;
    private List<CandidateInfo> candidates;

    @Data
    public static class CandidateInfo implements Serializable {
        private String id;
        private String name;
        private String email;
    }
}
