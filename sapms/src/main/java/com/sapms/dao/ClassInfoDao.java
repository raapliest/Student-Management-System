package com.sapms.dao;
import com.sapms.entity.ClassInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class ClassInfoDao {
    @Autowired private JdbcTemplate jdbc;
    public ClassInfo findAvailable(String majorNo) {
        String sql = "SELECT class_no,major_no,class_name,current_seq,max_count,student_count FROM class_info WHERE major_no=? AND student_count<max_count ORDER BY class_no LIMIT 1";
        return jdbc.query(sql, rs -> {
            if (rs.next()) {
                ClassInfo c = new ClassInfo();
                c.setClassNo(rs.getString("class_no")); c.setMajorNo(rs.getString("major_no"));
                c.setClassName(rs.getString("class_name")); c.setCurrentSeq(rs.getString("current_seq"));
                c.setMaxCount(rs.getInt("max_count")); c.setStudentCount(rs.getInt("student_count"));
                return c;
            } return null;
        }, majorNo);
    }
    public void updateSeq(String majorNo, String classNo, String newSeq) {
        jdbc.update("UPDATE class_info SET current_seq=?, student_count=student_count+1 WHERE major_no=? AND class_no=?", newSeq, majorNo, classNo);
    }
}