package com.sapms.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class GpaService {
    @Autowired private JdbcTemplate jdbc;

    public Map<String,Object> getStudentGpa(String studentNo) {
        List<Map<String,Object>> grades = jdbc.queryForList(
            "SELECT c.course_name, c.course_code, c.credit_hours, g.total_score, g.grade_letter, g.status " +
            "FROM grade g JOIN course c ON g.course_id=c.id " +
            "WHERE g.student_no=? AND g.total_score IS NOT NULL", studentNo);

        double totalPoints = 0; int totalCredits = 0;
        for (var g : grades) {
            double score = Double.parseDouble(g.get("total_score").toString());
            int credits = Integer.parseInt(g.get("credit_hours").toString());
            double gradePoint = scoreToGradePoint(score);
            totalPoints += gradePoint * credits;
            totalCredits += credits;
        }

        double gpa = totalCredits > 0 ? Math.round(totalPoints / totalCredits * 100.0) / 100.0 : 0.0;
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("gpa", gpa);
        result.put("totalCredits", totalCredits);
        result.put("gradeCount", grades.size());
        result.put("courses", grades);
        result.put("gpaLabel", gpaLabel(gpa));
        return result;
    }

    private double scoreToGradePoint(double score) {
        if (score >= 90) return 4.0;
        if (score >= 80) return 3.0;
        if (score >= 70) return 2.0;
        if (score >= 60) return 1.0;
        return 0.0;
    }

    private String gpaLabel(double gpa) {
        if (gpa >= 3.5) return "Cum Laude";
        if (gpa >= 3.0) return "Very Good";
        if (gpa >= 2.5) return "Good";
        if (gpa >= 2.0) return "Satisfactory";
        if (gpa > 0)    return "Needs Improvement";
        return "No Data";
    }
}
