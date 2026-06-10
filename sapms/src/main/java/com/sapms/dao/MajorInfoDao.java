package com.sapms.dao;
import com.sapms.entity.MajorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class MajorInfoDao {
    @Autowired private JdbcTemplate jdbc;
    public List<MajorInfo> findAll() {
        return jdbc.query("SELECT major_no, major_name FROM major_info ORDER BY major_no",
            (rs, i) -> { MajorInfo m = new MajorInfo(); m.setMajorNo(rs.getString("major_no")); m.setMajorName(rs.getString("major_name")); return m; });
    }
}