package com.sapms.dao;
import com.sapms.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class StudentDao {
    @Autowired private JdbcTemplate jdbc;

    private Student map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Student s = new Student();
        s.setStudentNo(rs.getString("student_no")); s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email")); s.setMajorNo(rs.getString("major_no"));
        s.setClassNo(rs.getString("class_no")); s.setStudentSeq(rs.getString("student_seq"));
        s.setAcademicYearId(rs.getLong("academic_year_id")); s.setUserId(rs.getLong("user_id"));
        try { s.setMajorName(rs.getString("major_name")); } catch (Exception e) {}
        try { s.setClassName(rs.getString("class_name")); } catch (Exception e) {}
        try { s.setAcademicYearLabel(rs.getString("year_label")); } catch (Exception e) {}
        return s;
    }

    private static final String BASE_SQL = "SELECT s.*,m.major_name,c.class_name,a.year_label FROM student s LEFT JOIN major_info m ON s.major_no=m.major_no LEFT JOIN class_info c ON s.major_no=c.major_no AND s.class_no=c.class_no LEFT JOIN academic_year a ON s.academic_year_id=a.id";

    public void insert(Student s) {
        jdbc.update("INSERT INTO student(student_no,user_id,full_name,email,major_no,class_no,student_seq,academic_year_id) VALUES(?,?,?,?,?,?,?,?)",
            s.getStudentNo(), s.getUserId(), s.getFullName(), s.getEmail(), s.getMajorNo(), s.getClassNo(), s.getStudentSeq(), s.getAcademicYearId());
    }

    public List<Student> findAll() {
        return jdbc.query(BASE_SQL + " ORDER BY s.student_no", this::map);
    }

    public List<Student> search(String keyword, String majorNo) {
        StringBuilder sql = new StringBuilder(BASE_SQL + " WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (s.full_name LIKE ? OR s.student_no LIKE ? OR s.email LIKE ?)");
            String k = "%" + keyword + "%";
            params.add(k); params.add(k); params.add(k);
        }
        if (majorNo != null && !majorNo.isBlank()) {
            sql.append(" AND s.major_no = ?");
            params.add(majorNo);
        }
        sql.append(" ORDER BY s.student_no");
        return jdbc.query(sql.toString(), this::map, params.toArray());
    }

    public Student findByNo(String no) {
        return jdbc.query(BASE_SQL + " WHERE s.student_no=?", rs -> rs.next() ? map(rs, 0) : null, no);
    }

    public Student findByUserId(Long userId) {
        return jdbc.query(BASE_SQL + " WHERE s.user_id=?", rs -> rs.next() ? map(rs, 0) : null, userId);
    }

    public void update(String studentNo, String fullName, String email) {
        jdbc.update("UPDATE student SET full_name=?,email=? WHERE student_no=?", fullName, email, studentNo);
    }

    public void delete(String studentNo) {
        Long userId = jdbc.queryForObject("SELECT user_id FROM student WHERE student_no=?", Long.class, studentNo);
        jdbc.update("DELETE FROM attendance WHERE student_no=?", studentNo);
        jdbc.update("DELETE FROM grade WHERE student_no=?", studentNo);
        jdbc.update("DELETE FROM student WHERE student_no=?", studentNo);
        jdbc.update("DELETE FROM users WHERE id=?", userId);
    }

    public int countAll() { return jdbc.queryForObject("SELECT COUNT(*) FROM student", Integer.class); }
}
