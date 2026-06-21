package com.sapms.dao;

import com.sapms.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class AttendanceDao {

    @Autowired
    private JdbcTemplate jdbc;

    private Attendance map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getLong("id"));
        a.setStudentNo(rs.getString("student_no"));
        a.setCourseId(rs.getLong("course_id"));
        a.setSessionDate(rs.getDate("session_date").toLocalDate());
        a.setStatus(rs.getString("status"));
        a.setNote(rs.getString("note"));
        try {
            a.setStudentName(rs.getString("full_name"));
        } catch (Exception ignored) {
        }
        try {
            a.setCourseCode(rs.getString("course_code"));
        } catch (Exception ignored) {
        }
        try {
            a.setCourseName(rs.getString("course_name"));
        } catch (Exception ignored) {
        }
        return a;
    }

    /**
     * Returns the generated ID — needed for Command pattern undo support
     */
    public Long insert(Attendance a) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO attendance(student_no,course_id,session_date,status,note) VALUES(?,?,?,?,?)",
                    new String[]{"id"});
            ps.setString(1, a.getStudentNo());
            ps.setLong(2, a.getCourseId());
            ps.setDate(3, java.sql.Date.valueOf(a.getSessionDate()));
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getNote());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM attendance WHERE id=?", id);
    }

    public List<Attendance> findByCourse(Long courseId) {
        String sql = "SELECT a.*,s.full_name,c.course_code,c.course_name FROM attendance a " +
                "JOIN student s ON a.student_no=s.student_no " +
                "JOIN course c ON a.course_id=c.id " +
                "WHERE a.course_id=? ORDER BY a.session_date DESC";
        return jdbc.query(sql, this::map, courseId);
    }

    public List<Attendance> findByStudent(String studentNo) {
        String sql = "SELECT a.*,s.full_name,c.course_code,c.course_name FROM attendance a " +
                "JOIN student s ON a.student_no=s.student_no " +
                "JOIN course c ON a.course_id=c.id " +
                "WHERE a.student_no=? ORDER BY a.session_date DESC";
        return jdbc.query(sql, this::map, studentNo);
    }

    public java.util.Map<String, Object> getAttendanceSummary(String studentNo, Long courseId) {
        return jdbc.queryForMap(
                "SELECT COUNT(*) as total, " +
                        "SUM(CASE WHEN status='PRESENT' THEN 1 ELSE 0 END) as present_count, " +
                        "SUM(CASE WHEN status='ABSENT' THEN 1 ELSE 0 END) as absent_count, " +
                        "SUM(CASE WHEN status='LATE' THEN 1 ELSE 0 END) as late_count " +
                        "FROM attendance WHERE student_no=? AND course_id=?", studentNo, courseId);
    }
}
