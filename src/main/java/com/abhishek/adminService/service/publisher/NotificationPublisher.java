package com.abhishek.adminService.service.publisher;

import com.abhishek.adminService.dto.event.TestAssignedEvent;
import com.abhishek.adminService.dto.event.TestScheduledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final JmsTemplate jmsTemplate;

    private static final String QUEUE_TEST_ASSIGNED = "test.assigned";
    private static final String QUEUE_TEST_SCHEDULED = "test.scheduled";

    public void publishTestAssignedEvent(TestAssignedEvent event) {
        try {
            log.info("Publishing TEST_ASSIGNED event for test: {}", event.getTestName());
            jmsTemplate.convertAndSend(QUEUE_TEST_ASSIGNED, event);
            log.debug("Event published successfully: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish TEST_ASSIGNED event", e);
        }
    }

    public void publishTestScheduledEvent(TestScheduledEvent event) {
        try {
            log.info("Publishing TEST_SCHEDULED event for test: {}", event.getTestName());
            jmsTemplate.convertAndSend(QUEUE_TEST_SCHEDULED, event);
            log.debug("Event published successfully: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish TEST_SCHEDULED event", e);
        }
    }
}
