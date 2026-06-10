package com.sapms.pattern.command;

import com.sapms.dao.AttendanceDao;
import com.sapms.entity.Attendance;
import java.time.LocalDate;

/**
 * DESIGN PATTERN: Command — Concrete Command
 * Encapsulates a "record attendance" operation as an object.
 */
public class RecordAttendanceCommand implements Command {

    private final AttendanceDao attendanceDao;
    private final String studentNo;
    private final Long courseId;
    private final LocalDate sessionDate;
    private final String status;
    private final String note;

    private Long insertedId; // for undo

    public RecordAttendanceCommand(AttendanceDao attendanceDao, String studentNo,
                                    Long courseId, LocalDate sessionDate,
                                    String status, String note) {
        this.attendanceDao = attendanceDao;
        this.studentNo     = studentNo;
        this.courseId      = courseId;
        this.sessionDate   = sessionDate;
        this.status        = status;
        this.note          = note;
    }

    @Override
    public void execute() {
        Attendance a = new Attendance();
        a.setStudentNo(studentNo);
        a.setCourseId(courseId);
        a.setSessionDate(sessionDate);
        a.setStatus(status);
        a.setNote(note);
        insertedId = attendanceDao.insert(a);
        System.out.println("✅ COMMAND EXECUTE: " + getDescription());
    }

    @Override
    public void undo() {
        if (insertedId != null) {
            attendanceDao.deleteById(insertedId);
            System.out.println("↩️  COMMAND UNDO: " + getDescription());
        }
    }

    @Override
    public String getDescription() {
        return String.format("RecordAttendance[student=%s, course=%d, date=%s, status=%s]",
            studentNo, courseId, sessionDate, status);
    }
}
