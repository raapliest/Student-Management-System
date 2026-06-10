package com.sapms.dao;
import com.sapms.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class NotificationDao {
    @Autowired private JdbcTemplate jdbc;

    private Notification map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id")); n.setUserId(rs.getLong("user_id"));
        n.setMessage(rs.getString("message")); n.setRead(rs.getInt("is_read")==1);
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return n;
    }

    public List<Notification> findByUser(Long userId) {
        return jdbc.query("SELECT * FROM notification WHERE user_id=? ORDER BY created_at DESC LIMIT 20", this::map, userId);
    }

    public int countUnread(Long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM notification WHERE user_id=? AND is_read=0", Integer.class, userId);
    }

    public void markAllRead(Long userId) {
        jdbc.update("UPDATE notification SET is_read=1 WHERE user_id=?", userId);
    }

    public void insert(Long userId, String message) {
        jdbc.update("INSERT INTO notification(user_id,message) VALUES(?,?)", userId, message);
    }
}