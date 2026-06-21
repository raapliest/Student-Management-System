package com.sapms.pattern.strategy;

public class FourPointGpaStrategy implements GpaStrategy {

    @Override
    public double scoreToPoints(double totalScore) {
        if (totalScore >= 90) return 4.0;
        if (totalScore >= 80) return 3.5;
        if (totalScore >= 70) return 3.0;
        if (totalScore >= 60) return 2.0;
        if (totalScore >= 50) return 1.0;
        return 0.0;
    }

    @Override
    public String getSchemeName() {
        return "4.0 Scale";
    }
}
