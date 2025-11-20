package com.abhishek.adminService.service;

import com.abhishek.adminService.model.TestDef;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationClient {
    // In a real system this would send messages to RabbitMQ or call Notification Service HTTP API.
    public void notifyAssigned(TestDef t, List<String> candidates) {
        // stub: log or no-op
        System.out.println("[NotificationClient] Assigned test " + t.getId() + " to " + candidates);
    }

    public void notifyTestStarted(TestDef t) {
        System.out.println("[NotificationClient] Test started: " + t.getId());
    }

    public void notifyTestEnded(TestDef t) {
        System.out.println("[NotificationClient] Test ended: " + t.getId());
    }
}