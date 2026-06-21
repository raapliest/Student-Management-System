package com.sapms.pattern.strategy;

public interface GpaStrategy {
    double scoreToPoints(double totalScore);

    String getSchemeName();
}
