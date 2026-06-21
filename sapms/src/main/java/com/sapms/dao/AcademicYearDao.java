package com.sapms.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AcademicYearDao {
    @Autowired
    private JdbcTemplate jdbc;

    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM academic_year ORDER BY id DESC");
    }

    public void insert(String startYear, String endYear) {
        startYear = startYear == null ? "" : startYear.replaceAll("[^0-9]", "").trim();
        if (startYear.isEmpty()) throw new IllegalArgumentException("Start year is required");
        String label;
        if (endYear == null || endYear.isBlank()) {
            int start = Integer.parseInt(startYear);
            label = start + "/" + (start + 1);
        } else {
            endYear = endYear.replaceAll("[^0-9]", "").trim();
            label = startYear + "/" + endYear;
        }
        System.out.println("DEBUG: Inserting academic year label = [" + label + "]");
        jdbc.update("INSERT INTO academic_year(year_label, is_active) VALUES(?,0)", label);
    }

    public void setActive(Long id) {
        jdbc.update("UPDATE academic_year SET is_active=0 WHERE id != ?", id);
        jdbc.update("UPDATE academic_year SET is_active=1 WHERE id=?", id);
    }

    public boolean delete(Long id) {
        try {
            int rows = jdbc.update("DELETE FROM academic_year WHERE id=? AND is_active=0", id);
            return rows > 0;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Foreign key constraint — students/courses still reference this year
            return false;
        }
    }
}
