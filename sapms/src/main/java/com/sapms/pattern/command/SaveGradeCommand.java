package com.sapms.pattern.command;

import com.sapms.dao.GradeDao;
import com.sapms.entity.Grade;
import com.sapms.util.GradeUtil;

import java.math.BigDecimal;

/**
 * DESIGN PATTERN: Command — Concrete Command
 * Encapsulates a "save grade" operation as an object.
 * Can be executed, undone, queued, or logged.
 */
public class SaveGradeCommand implements Command {

    private final GradeDao gradeDao;
    private final String studentNo;
    private final Long courseId;
    private final BigDecimal assignment, midterm, finalScore;

    // Previous state for undo
    private Grade previousGrade;

    public SaveGradeCommand(GradeDao gradeDao, String studentNo, Long courseId,
                            BigDecimal assignment, BigDecimal midterm, BigDecimal finalScore) {
        this.gradeDao = gradeDao;
        this.studentNo = studentNo;
        this.courseId = courseId;
        this.assignment = assignment;
        this.midterm = midterm;
        this.finalScore = finalScore;
    }

    @Override
    public void execute() {
        // Save previous state for undo
        previousGrade = gradeDao.findByStudentAndCourse(studentNo, courseId);

        // Build and save new grade
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
        System.out.println("✅ COMMAND EXECUTE: " + getDescription());
    }

    @Override
    public void undo() {
        if (previousGrade != null) {
            gradeDao.upsert(previousGrade);
            System.out.println("↩️  COMMAND UNDO: " + getDescription());
        } else {
            gradeDao.deleteByStudentAndCourse(studentNo, courseId);
            System.out.println("↩️  COMMAND UNDO (delete): " + getDescription());
        }
    }

    @Override
    public String getDescription() {
        return String.format("SaveGrade[student=%s, course=%d, A=%.1f, M=%.1f, F=%.1f]",
                studentNo, courseId,
                assignment != null ? assignment.doubleValue() : 0,
                midterm != null ? midterm.doubleValue() : 0,
                finalScore != null ? finalScore.doubleValue() : 0);
    }
}
