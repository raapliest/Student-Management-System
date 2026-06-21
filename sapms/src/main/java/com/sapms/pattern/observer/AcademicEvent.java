package com.sapms.pattern.observer;

import java.time.LocalDateTime;

/**
 * DESIGN PATTERN: Observer — Event object
 * Carries event data from Publisher to all Subscribers.
 */
public class AcademicEvent {

    public enum Type {
        STUDENT_AT_RISK,
        GRADE_SUBMITTED,
        ATTENDANCE_LOW,
        STUDENT_REGISTERED,
        GRADE_IMPROVED
    }

    private final Type type;
    private final String studentNo;
    private final String message;
    private final LocalDateTime timestamp;

    public AcademicEvent(Type type, String studentNo, String message) {
        this.type = type;
        this.studentNo = studentNo;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s — %s (%s)", timestamp, type, studentNo, message);
    }
}
