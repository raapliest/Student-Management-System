package com.sapms.dao;
import com.sapms.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class CourseDao {
    @Autowired private JdbcTemplate jdbc;

    private Course map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Course c = new Course();
        c.setId(rs.getLong("id")); c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name")); c.setCreditHours(rs.getInt("credit_hours"));
        c.setMajorNo(rs.getString("major_no")); c.setAcademicYearId(rs.getLong("academic_year_id"));
        try { c.setLecturerId(rs.getLong("lecturer_id")); } catch (Exception e) {}
        try { c.setMajorName(rs.getString("major_name")); } catch (Exception e) {}
        try { c.setLecturerName(rs.getString("lecturer_name")); } catch (Exception e) {}
        try { c.setAcademicYearLabel(rs.getString("year_label")); } catch (Exception e) {}
        return c;
    }

    public List<Course> findAll() {
        String sql = "SELECT c.*,m.major_name,l.full_name as lecturer_name,a.year_label FROM course c LEFT JOIN major_info m ON c.major_no=m.major_no LEFT JOIN lecturer l ON c.lecturer_id=l.id LEFT JOIN academic_year a ON c.academic_year_id=a.id ORDER BY c.course_code";
        return jdbc.query(sql, this::map);
    }

    public List<Course> findByLecturer(Long lecturerId) {
        String sql = "SELECT c.*,m.major_name,l.full_name as lecturer_name,a.year_label FROM course c LEFT JOIN major_info m ON c.major_no=m.major_no LEFT JOIN lecturer l ON c.lecturer_id=l.id LEFT JOIN academic_year a ON c.academic_year_id=a.id WHERE c.lecturer_id=? ORDER BY c.course_code";
        return jdbc.query(sql, this::map, lecturerId);
    }

    public Course findById(Long id) {
        String sql = "SELECT c.*,m.major_name,l.full_name as lecturer_name,a.year_label FROM course c LEFT JOIN major_info m ON c.major_no=m.major_no LEFT JOIN lecturer l ON c.lecturer_id=l.id LEFT JOIN academic_year a ON c.academic_year_id=a.id WHERE c.id=?";
        return jdbc.query(sql, rs -> rs.next() ? map(rs, 0) : null, id);
    }

    public void insert(Course c) {
        jdbc.update("INSERT INTO course(course_code,course_name,credit_hours,major_no,academic_year_id,lecturer_id) VALUES(?,?,?,?,?,?)",
            c.getCourseCode(), c.getCourseName(), c.getCreditHours(), c.getMajorNo(), c.getAcademicYearId(), c.getLecturerId());
    }

    public void update(Course c) {
        jdbc.update("UPDATE course SET course_code=?,course_name=?,credit_hours=?,major_no=?,lecturer_id=? WHERE id=?",
            c.getCourseCode(), c.getCourseName(), c.getCreditHours(), c.getMajorNo(), c.getLecturerId(), c.getId());
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM attendance WHERE course_id=?", id);
        jdbc.update("DELETE FROM grade WHERE course_id=?", id);
        jdbc.update("DELETE FROM course WHERE id=?", id);
    }

    public int countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM course", Integer.class);
    }
}