package com.sapms.pattern.facade;

import com.sapms.util.GradeUtil;

import java.math.BigDecimal;

public class AcademicFacade {

    public GradeResult processGrade(BigDecimal assignment, BigDecimal midterm, BigDecimal finalScore) {
        BigDecimal total = GradeUtil.calculateTotal(assignment, midterm, finalScore);
        String gradeLetter = GradeUtil.calculateGradeLetter(total);
        String status = GradeUtil.calculateStatus(total);
        return new GradeResult(total, gradeLetter, status);
    }

    public static class GradeResult {
        public final BigDecimal total;
        public final String gradeLetter;
        public final String status;

        public GradeResult(BigDecimal total, String gradeLetter, String status) {
            this.total = total;
            this.gradeLetter = gradeLetter;
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("total=%.2f, grade=%s, status=%s", total, gradeLetter, status);
        }
    }
}
