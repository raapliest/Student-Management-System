package com.sapms.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DESIGN PATTERN: Observer — Publisher (Subject)
 * <p>
 * Central event bus for academic events. Listeners register themselves
 * and get notified whenever a relevant event is published.
 * <p>
 * Example:
 * publisher.subscribe(AcademicEvent.Type.STUDENT_AT_RISK, myListener);
 * publisher.publish(new AcademicEvent(Type.STUDENT_AT_RISK, "00101001", "Score 45"));
 */
@Component
public class AcademicEventPublisher {

    // Map of event type → list of listeners
    private final Map<AcademicEvent.Type, List<AcademicEventListener>> listeners
            = new ConcurrentHashMap<>();

    // Keep event history (last 100 events)
    private final List<AcademicEvent> eventHistory = new ArrayList<>();
    private static final int MAX_HISTORY = 100;

    /**
     * Register a listener for a specific event type
     */
    public void subscribe(AcademicEvent.Type type, AcademicEventListener listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unregister a listener
     */
    public void unsubscribe(AcademicEvent.Type type, AcademicEventListener listener) {
        List<AcademicEventListener> list = listeners.get(type);
        if (list != null) list.remove(listener);
    }

    /**
     * Publish an event — notifies ALL registered listeners for this event type
     */
    public void publish(AcademicEvent event) {
        // Store in history
        synchronized (eventHistory) {
            if (eventHistory.size() >= MAX_HISTORY) eventHistory.remove(0);
            eventHistory.add(event);
        }

        // Notify subscribers
        List<AcademicEventListener> subs = listeners.getOrDefault(event.getType(), List.of());
        for (AcademicEventListener listener : subs) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                System.err.println("Listener error: " + e.getMessage());
            }
        }
    }

    public List<AcademicEvent> getHistory() {
        return new ArrayList<>(eventHistory);
    }

    public List<AcademicEvent> getHistoryByType(AcademicEvent.Type type) {
        return eventHistory.stream()
                .filter(e -> e.getType() == type)
                .collect(java.util.stream.Collectors.toList());
    }
}
