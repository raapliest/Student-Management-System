package com.sapms.pattern.decorator;

import com.sapms.entity.Grade;
import java.math.BigDecimal;
import java.util.List;

/**
 * DESIGN PATTERN: Decorator — Component Interface
 * Defines operations that can be decorated.
 */
public interface GradeOperation {
    void saveGrade(String studentNo, Long courseId, BigDecimal assignment,
                   BigDecimal midterm, BigDecimal finalScore);
    List<Grade> getGradesByStudent(String studentNo);
    List<Grade> getGradesByCourse(Long courseId);
}
