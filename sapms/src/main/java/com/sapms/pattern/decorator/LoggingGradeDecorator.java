package com.sapms.pattern.decorator;

import com.sapms.entity.Grade;
import com.sapms.pattern.observer.AcademicEvent;
import com.sapms.pattern.observer.AcademicEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: Decorator — Concrete Decorator
 *
 * Wraps a GradeOperation and adds:
 *   1. Audit logging of every save operation
 *   2. Automatic at-risk event publishing via Observer pattern
 *
 * The original GradeOperation is not modified at all.
 */
@Component("loggingGradeDecorator")
public class LoggingGradeDecorator implements GradeOperation {

    // The wrapped component
    private final GradeOperation wrapped;
    private final AcademicEventPublisher eventPublisher;

    // Audit log of all grade save operations
    private static final List<String> auditLog = new ArrayList<>();

    @Autowired
    public LoggingGradeDecorator(
            @Qualifier("baseGradeOperation") GradeOperation wrapped,
            AcademicEventPublisher eventPublisher) {
        this.wrapped        = wrapped;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveGrade(String studentNo, Long courseId, BigDecimal assignment,
                          BigDecimal midterm, BigDecimal finalScore) {
        String logEntry = String.format("[%s] GRADE_SAVE | student=%s | course=%d | A=%.1f M=%.1f F=%.1f",
            LocalDateTime.now(), studentNo, courseId,
            assignment  != null ? assignment.doubleValue()  : 0,
            midterm     != null ? midterm.doubleValue()     : 0,
            finalScore  != null ? finalScore.doubleValue()  : 0);

        System.out.println("📝 AUDIT: " + logEntry);
        auditLog.add(logEntry);

        // Delegate to wrapped component
        wrapped.saveGrade(studentNo, courseId, assignment, midterm, finalScore);

        // Post-save: check if student is now at-risk and fire Observer event
        List<Grade> grades = wrapped.getGradesByCourse(courseId);
        grades.stream()
            .filter(g -> studentNo.equals(g.getStudentNo()))
            .findFirst()
            .ifPresent(g -> {
                if ("AT_RISK".equals(g.getStatus())) {
                    eventPublisher.publish(new AcademicEvent(
                        AcademicEvent.Type.STUDENT_AT_RISK, studentNo,
                        "Score " + g.getTotalScore() + " — at risk of failing"));
                } else if ("FAIL".equals(g.getStatus())) {
                    eventPublisher.publish(new AcademicEvent(
                        AcademicEvent.Type.GRADE_SUBMITTED, studentNo,
                        "Failed with score " + g.getTotalScore()));
                }
            });
    }

    @Override
    public List<Grade> getGradesByStudent(String studentNo) {
        return wrapped.getGradesByStudent(studentNo);
    }

    @Override
    public List<Grade> getGradesByCourse(Long courseId) {
        return wrapped.getGradesByCourse(courseId);
    }

    public static List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
