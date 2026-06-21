package com.sapms.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GradeUtil {
    public static BigDecimal calculateTotal(BigDecimal assignment, BigDecimal midterm, BigDecimal finalScore) {
        if (assignment == null) assignment = BigDecimal.ZERO;
        if (midterm == null) midterm = BigDecimal.ZERO;
        if (finalScore == null) finalScore = BigDecimal.ZERO;
        // Weight: assignment 20%, midterm 30%, final 50%
        BigDecimal total = assignment.multiply(new BigDecimal("0.20"))
                .add(midterm.multiply(new BigDecimal("0.30")))
                .add(finalScore.multiply(new BigDecimal("0.50")));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static String calculateGradeLetter(BigDecimal total) {
        if (total == null) return "-";
        double score = total.doubleValue();
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 51) return "D";
        return "F";
    }

    public static String calculateStatus(BigDecimal total) {
        if (total == null) return "ACTIVE";
        double score = total.doubleValue();
        if (score >= 75) return "PASS";
        if (score >= 51) return "AT_RISK";
        return "FAIL";
    }
}