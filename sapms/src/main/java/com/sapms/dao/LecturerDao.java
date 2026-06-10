package com.sapms.dao;
import com.sapms.entity.Lecturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class LecturerDao {
    @Autowired private JdbcTemplate jdbc;

    private Lecturer map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Lecturer l = new Lecturer(); l.setId(rs.getLong("id")); l.setUserId(rs.getLong("user_id"));
        l.setLecturerNo(rs.getString("lecturer_no")); l.setFullName(rs.getString("full_name")); l.setEmail(rs.getString("email"));
        return l;
    }

    public List<Lecturer> findAll() { return jdbc.query("SELECT * FROM lecturer ORDER BY lecturer_no", this::map); }

    public Lecturer findById(Long id) {
        return jdbc.query("SELECT * FROM lecturer WHERE id=?", rs -> rs.next() ? map(rs,0) : null, id);
    }

    public Lecturer findByUserId(Long userId) {
        return jdbc.query("SELECT * FROM lecturer WHERE user_id=?", rs -> rs.next() ? map(rs,0) : null, userId);
    }

    public void insert(Lecturer l) {
        jdbc.update("INSERT INTO lecturer(user_id,lecturer_no,full_name,email) VALUES(?,?,?,?)",
            l.getUserId(), l.getLecturerNo(), l.getFullName(), l.getEmail());
    }

    public void update(Lecturer l) {
        jdbc.update("UPDATE lecturer SET full_name=?,email=? WHERE id=?", l.getFullName(), l.getEmail(), l.getId());
    }

    public void delete(Long id) {
        Long userId = jdbc.queryForObject("SELECT user_id FROM lecturer WHERE id=?", Long.class, id);
        jdbc.update("UPDATE course SET lecturer_id=NULL WHERE lecturer_id=?", id);
        jdbc.update("DELETE FROM lecturer WHERE id=?", id);
        jdbc.update("DELETE FROM users WHERE id=?", userId);
    }

    public int countAll() { return jdbc.queryForObject("SELECT COUNT(*) FROM lecturer", Integer.class); }
}