package com.sapms.pattern.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class GpaCalculator {
    private final GpaStrategy strategy;

    public GpaCalculator(GpaStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal calculateGpa(List<BigDecimal> scores, List<Integer> credits) {
        if (scores == null || scores.isEmpty()) return BigDecimal.ZERO;

        double weightedSum = 0;
        int totalCredits = 0;

        for (int i = 0; i < scores.size(); i++) {
            double points = strategy.scoreToPoints(scores.get(i).doubleValue());
            int credit = credits.get(i);
            weightedSum += points * credit;
            totalCredits += credit;
        }

        if (totalCredits == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(weightedSum / totalCredits).setScale(2, RoundingMode.HALF_UP);
    }

    public String getSchemeName() {
        return strategy.getSchemeName();
    }
}
