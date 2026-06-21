package com.sapms.service;

import com.sapms.pattern.strategy.GpaCalculator;
import com.sapms.pattern.strategy.FourPointGpaStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GpaService {

    @Autowired
    private JdbcTemplate jdbc;


    private final GpaCalculator gpaCalculator = new GpaCalculator(new FourPointGpaStrategy());

    public Map<String, Object> getStudentGpa(String studentNo) {
        List<Map<String, Object>> grades = jdbc.queryForList(
                "SELECT c.course_name, c.course_code, c.credit_hours, g.total_score, g.grade_letter, g.status " +
                        "FROM grade g JOIN course c ON g.course_id=c.id " +
                        "WHERE g.student_no=? AND g.total_score IS NOT NULL", studentNo);

        List<BigDecimal> scores = new ArrayList<>();
        List<Integer> credits = new ArrayList<>();
        for (var g : grades) {
            if (g.get("credit_hours") == null) continue;
            scores.add(new BigDecimal(g.get("total_score").toString()));
            credits.add(Integer.parseInt(g.get("credit_hours").toString()));
        }

        BigDecimal gpa = gpaCalculator.calculateGpa(scores, credits);
        int totalCredits = credits.stream().mapToInt(Integer::intValue).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("gpa", gpa.doubleValue());
        result.put("totalCredits", totalCredits);
        result.put("gradeCount", grades.size());
        result.put("courses", grades);
        result.put("gpaLabel", gpaLabel(gpa.doubleValue()));
        result.put("gpaScheme", gpaCalculator.getSchemeName());
        return result;
    }

    private String gpaLabel(double gpa) {
        if (gpa >= 3.5) return "Cum Laude";
        if (gpa >= 3.0) return "Very Good";
        if (gpa >= 2.5) return "Good";
        if (gpa >= 2.0) return "Satisfactory";
        if (gpa > 0) return "Needs Improvement";
        return "No Data";
    }
}
