package com.sapms.pattern.decorator;

import com.sapms.dao.GradeDao;
import com.sapms.entity.Grade;
import com.sapms.util.GradeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/**
 * DESIGN PATTERN: Decorator — Concrete Component
 * The base implementation without any decoration.
 */
@Component("baseGradeOperation")
public class BaseGradeOperation implements GradeOperation {

    @Autowired
    private GradeDao gradeDao;

    @Override
    public void saveGrade(String studentNo, Long courseId, BigDecimal assignment,
                          BigDecimal midterm, BigDecimal finalScore) {
        Grade g = new Grade();
        g.setStudentNo(studentNo);
        g.setCourseId(courseId);
        g.setAssignmentScore(assignment);
        g.setMidtermScore(midterm);
        g.setFinalScore(finalScore);

        BigDecimal total = GradeUtil.calculateTotal(assignment, midterm, finalScore);
        g.setTotalScore(total);
        g.setGradeLetter(GradeUtil.calculateGradeLetter(total));
        g.setStatus(GradeUtil.calculateStatus(total));

        gradeDao.upsert(g);
    }

    @Override
    public List<Grade> getGradesByStudent(String studentNo) {
        return gradeDao.findByStudent(studentNo);
    }

    @Override
    public List<Grade> getGradesByCourse(Long courseId) {
        return gradeDao.findByCourse(courseId);
    }
}
