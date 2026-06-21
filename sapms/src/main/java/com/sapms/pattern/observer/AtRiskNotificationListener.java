package com.sapms.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: Observer — Concrete Subscriber
 * Listens for AT_RISK and LOW_ATTENDANCE events and logs them.
 * In a real system, this could send emails or push notifications.
 */
@Component
public class AtRiskNotificationListener implements AcademicEventListener {

    private final List<String> notifications = new java.util.ArrayList<>();

    @Override
    public void onEvent(AcademicEvent event) {
        String msg = String.format("[ALERT] %s: %s — %s",
                event.getType(), event.getStudentNo(), event.getMessage());
        notifications.add(msg);
        System.out.println("📢 Academic Alert: " + msg);
    }

    public List<String> getNotifications() {
        return new java.util.ArrayList<>(notifications);
    }
}
