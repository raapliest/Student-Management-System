package com.sapms.pattern.observer;

/**
 * DESIGN PATTERN: Observer — Subscriber interface
 * Any class that wants to receive academic events must implement this.
 */
public interface AcademicEventListener {
    void onEvent(AcademicEvent event);
}
