package com.sapms.dao;

import com.sapms.entity.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class GradeDao {

    @Autowired private JdbcTemplate jdbc;

    private Grade map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Grade g = new Grade();
        g.setId(rs.getLong("id"));
        g.setStudentNo(rs.getString("student_no"));
        g.setCourseId(rs.getLong("course_id"));
        g.setAssignmentScore(rs.getBigDecimal("assignment_score"));
        g.setMidtermScore(rs.getBigDecimal("midterm_score"));
        g.setFinalScore(rs.getBigDecimal("final_score"));
        g.setTotalScore(rs.getBigDecimal("total_score"));
        g.setGradeLetter(rs.getString("grade_letter"));
        g.setStatus(rs.getString("status"));
        try { g.setStudentName(rs.getString("full_name")); }   catch (Exception ignored) {}
        try { g.setCourseCode(rs.getString("course_code")); }  catch (Exception ignored) {}
        try { g.setCourseName(rs.getString("course_name")); }  catch (Exception ignored) {}
        return g;
    }

    public void upsert(Grade g) {
        Grade existing = findByStudentAndCourse(g.getStudentNo(), g.getCourseId());
        if (existing != null) {
            jdbc.update("UPDATE grade SET assignment_score=?,midterm_score=?,final_score=?,total_score=?,grade_letter=?,status=? WHERE student_no=? AND course_id=?",
                g.getAssignmentScore(), g.getMidtermScore(), g.getFinalScore(),
                g.getTotalScore(), g.getGradeLetter(), g.getStatus(),
                g.getStudentNo(), g.getCourseId());
        } else {
            jdbc.update("INSERT INTO grade(student_no,course_id,assignment_score,midterm_score,final_score,total_score,grade_letter,status) VALUES(?,?,?,?,?,?,?,?)",
                g.getStudentNo(), g.getCourseId(),
                g.getAssignmentScore(), g.getMidtermScore(), g.getFinalScore(),
                g.getTotalScore(), g.getGradeLetter(), g.getStatus());
        }
    }

    public Grade findByStudentAndCourse(String studentNo, Long courseId) {
        String sql = "SELECT g.*,s.full_name,c.course_code,c.course_name FROM grade g " +
            "LEFT JOIN student s ON g.student_no=s.student_no " +
            "LEFT JOIN course c ON g.course_id=c.id " +
            "WHERE g.student_no=? AND g.course_id=?";
        return jdbc.query(sql, rs -> rs.next() ? map(rs, 0) : null, studentNo, courseId);
    }

    public void deleteByStudentAndCourse(String studentNo, Long courseId) {
        jdbc.update("DELETE FROM grade WHERE student_no=? AND course_id=?", studentNo, courseId);
    }

    public List<Grade> findByStudent(String studentNo) {
        String sql = "SELECT g.*,s.full_name,c.course_code,c.course_name FROM grade g " +
            "LEFT JOIN student s ON g.student_no=s.student_no " +
            "LEFT JOIN course c ON g.course_id=c.id " +
            "WHERE g.student_no=? ORDER BY c.course_code";
        return jdbc.query(sql, this::map, studentNo);
    }

    public List<Grade> findByCourse(Long courseId) {
        String sql = "SELECT g.*,s.full_name,c.course_code,c.course_name FROM grade g " +
            "LEFT JOIN student s ON g.student_no=s.student_no " +
            "LEFT JOIN course c ON g.course_id=c.id " +
            "WHERE g.course_id=? ORDER BY g.student_no";
        return jdbc.query(sql, this::map, courseId);
    }
}
