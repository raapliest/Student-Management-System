package com.sapms.service;
import com.sapms.dao.*;
import com.sapms.entity.*;
import com.sapms.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class StudentRegisterService {
    @Autowired private ClassInfoDao classInfoDao;
    @Autowired private StudentDao studentDao;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Student register(String fullName, String email, String majorNo) {
        Long activeYearId = jdbc.queryForObject("SELECT id FROM academic_year WHERE is_active=1 LIMIT 1", Long.class);
        ClassInfo cls = classInfoDao.findAvailable(majorNo);
        if (cls == null) throw new RuntimeException("No available class under selected major");
        String nextSeq = NumberUtil.nextSequence(cls.getCurrentSeq(), 3);
        String studentNo = majorNo + cls.getClassNo() + nextSeq;
        String encodedPw = passwordEncoder.encode("123456");
        jdbc.update("INSERT INTO users(username,password,role) VALUES(?,?,'STUDENT')", studentNo, encodedPw);
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, studentNo);
        Student s = new Student();
        s.setStudentNo(studentNo); s.setUserId(userId); s.setFullName(fullName);
        s.setEmail(email); s.setMajorNo(majorNo); s.setClassNo(cls.getClassNo());
        s.setStudentSeq(nextSeq); s.setAcademicYearId(activeYearId);
        studentDao.insert(s);
        classInfoDao.updateSeq(majorNo, cls.getClassNo(), nextSeq);
        return s;
    }
}