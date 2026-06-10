package com.sapms.service;

import com.sapms.dao.GradeDao;
import com.sapms.entity.Grade;
import com.sapms.pattern.command.CommandInvoker;
import com.sapms.pattern.command.SaveGradeCommand;
import com.sapms.pattern.decorator.LoggingGradeDecorator;
import com.sapms.pattern.observer.AcademicEvent;
import com.sapms.pattern.observer.AcademicEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

/**
 * GradeService — orchestrates multiple design patterns:
 *
 *  - COMMAND    : SaveGradeCommand encapsulates save operation + undo support
 *  - DECORATOR  : LoggingGradeDecorator adds audit log + at-risk detection
 *  - OBSERVER   : Publishes GRADE_SUBMITTED event after save
 *  - SINGLETON  : ApplicationConfig for grade thresholds
 */
@Service
public class GradeService {

    @Autowired private GradeDao gradeDao;
    @Autowired private CommandInvoker commandInvoker;
    @Autowired private LoggingGradeDecorator loggingGradeDecorator;
    @Autowired private AcademicEventPublisher eventPublisher;

    public void saveGrade(String studentNo, Long courseId,
                          BigDecimal assignment, BigDecimal midterm, BigDecimal finalScore) {
        // COMMAND pattern — encapsulate operation, supports undo
        SaveGradeCommand cmd = new SaveGradeCommand(
            gradeDao, studentNo, courseId, assignment, midterm, finalScore);
        commandInvoker.execute(cmd);

        // DECORATOR pattern — logging + at-risk detection happens inside decorator
        loggingGradeDecorator.saveGrade(studentNo, courseId, assignment, midterm, finalScore);

        // OBSERVER pattern — publish grade submitted event
        eventPublisher.publish(new AcademicEvent(
            AcademicEvent.Type.GRADE_SUBMITTED, studentNo,
            "Grade submitted for course " + courseId));
    }

    public List<Grade> getGradesByStudent(String studentNo) {
        return gradeDao.findByStudent(studentNo);
    }

    public List<Grade> getGradesByCourse(Long courseId) {
        return gradeDao.findByCourse(courseId);
    }
}
