package com.sapms.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EnrollmentDao {
    @Autowired
    private JdbcTemplate jdbc;

    public void enroll(String studentNo, Long courseId) {
        jdbc.update("INSERT IGNORE INTO enrollment(student_no, course_id) VALUES(?,?)", studentNo, courseId);
    }

    public void unenroll(String studentNo, Long courseId) {
        jdbc.update("DELETE FROM enrollment WHERE student_no=? AND course_id=?", studentNo, courseId);
    }

    public boolean isEnrolled(String studentNo, Long courseId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM enrollment WHERE student_no=? AND course_id=?",
                Integer.class, studentNo, courseId);
        return count != null && count > 0;
    }

    public List<Long> findCourseIdsByStudent(String studentNo) {
        return jdbc.queryForList("SELECT course_id FROM enrollment WHERE student_no=?", Long.class, studentNo);
    }

    public int countByStudent(String studentNo) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM enrollment WHERE student_no=?", Integer.class, studentNo);
    }
}
