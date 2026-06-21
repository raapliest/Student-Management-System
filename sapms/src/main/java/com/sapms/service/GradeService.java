package com.sapms.service;

import com.sapms.dao.GradeDao;
import com.sapms.entity.Grade;
import com.sapms.pattern.command.CommandInvoker;
import com.sapms.pattern.command.SaveGradeCommand;
import com.sapms.pattern.decorator.LoggingGradeDecorator;
import com.sapms.pattern.facade.AcademicFacade;
import com.sapms.pattern.observer.AcademicEvent;
import com.sapms.pattern.observer.AcademicEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GradeService {

    @Autowired
    private GradeDao gradeDao;
    @Autowired
    private CommandInvoker commandInvoker;
    @Autowired
    private LoggingGradeDecorator loggingGradeDecorator;
    @Autowired
    private AcademicEventPublisher eventPublisher;


    private final AcademicFacade academicFacade = new AcademicFacade();

    public void saveGrade(String studentNo, Long courseId,
                          BigDecimal assignment, BigDecimal midterm, BigDecimal finalScore) {


        AcademicFacade.GradeResult preview = academicFacade.processGrade(assignment, midterm, finalScore);
        if ("AT_RISK".equals(preview.status) || "FAIL".equals(preview.status)) {
            System.out.println("[Facade] Pre-check — student " + studentNo
                    + " will be " + preview.status + " (" + preview + ") for course " + courseId);
        }


        SaveGradeCommand cmd = new SaveGradeCommand(
                gradeDao, studentNo, courseId, assignment, midterm, finalScore);
        commandInvoker.execute(cmd);


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
