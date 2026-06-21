package com.sapms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT username, password, role, enabled FROM users WHERE username = ?";
        try {
            return jdbc.queryForObject(sql, (rs, rowNum) -> {
                String pw = rs.getString("password");
                String role = rs.getString("role");
                boolean en = rs.getInt("enabled") == 1;
                return new User(username, pw, en, true, true, true,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            }, username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
