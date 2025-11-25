package com.abhishek.adminService.dto.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestScheduledEvent implements Serializable {
    private String eventType = "TEST_SCHEDULED";
    private String testId;
    private String testName;
    private LocalDateTime scheduledAt;
    private Integer duration;
    private List<String> candidateEmails;
}
