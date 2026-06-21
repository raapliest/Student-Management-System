package com.sapms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void updateStudentProfile(String studentNo, String fullName, String email) {
        jdbc.update("UPDATE student SET full_name=?, email=? WHERE student_no=?", fullName, email, studentNo);
    }

    public void updateLecturerProfile(Long lecturerId, String fullName, String email) {
        jdbc.update("UPDATE lecturer SET full_name=?, email=? WHERE id=?", fullName, email, lecturerId);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        String current = jdbc.queryForObject("SELECT password FROM users WHERE username=?", String.class, username);
        if (!passwordEncoder.matches(oldPassword, current)) return false;
        jdbc.update("UPDATE users SET password=? WHERE username=?", passwordEncoder.encode(newPassword), username);
        return true;
    }

    public Long getUserId(String username) {
        return jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, username);
    }
}