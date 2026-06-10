package com.sapms.dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class AcademicYearDao {
    @Autowired private JdbcTemplate jdbc;

    public List<Map<String,Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM academic_year ORDER BY id DESC");
    }

    public void insert(String yearLabel) {
        jdbc.update("INSERT INTO academic_year(year_label, is_active) VALUES(?,0)", yearLabel);
    }

    public void setActive(Long id) {
        jdbc.update("UPDATE academic_year SET is_active=0");
        jdbc.update("UPDATE academic_year SET is_active=1 WHERE id=?", id);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM academic_year WHERE id=? AND is_active=0", id);
    }
}
