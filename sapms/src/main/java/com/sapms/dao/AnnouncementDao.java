package com.sapms.dao;
import com.sapms.entity.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class AnnouncementDao {
    @Autowired private JdbcTemplate jdbc;

    private Announcement map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getLong("id")); a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content")); a.setCreatedBy(rs.getString("created_by"));
        a.setTargetRole(rs.getString("target_role"));
        a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return a;
    }

    public List<Announcement> findAll() {
        return jdbc.query("SELECT * FROM announcement ORDER BY created_at DESC", this::map);
    }

    public List<Announcement> findForRole(String role) {
        return jdbc.query("SELECT * FROM announcement WHERE target_role='ALL' OR target_role=? ORDER BY created_at DESC", this::map, role);
    }

    public void insert(Announcement a) {
        jdbc.update("INSERT INTO announcement(title,content,created_by,target_role) VALUES(?,?,?,?)",
            a.getTitle(), a.getContent(), a.getCreatedBy(), a.getTargetRole());
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM announcement WHERE id=?", id);
    }
}