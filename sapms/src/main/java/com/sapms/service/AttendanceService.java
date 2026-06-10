package com.sapms.service;
import com.sapms.dao.AttendanceDao;
import com.sapms.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Service
public class AttendanceService {
    @Autowired private AttendanceDao attendanceDao;

    public List<Attendance> getByCourse(Long courseId) { return attendanceDao.findByCourse(courseId); }
    public List<Attendance> getByStudent(String studentNo) { return attendanceDao.findByStudent(studentNo); }

    public void record(String studentNo, Long courseId, LocalDate date, String status, String note) {
        Attendance a = new Attendance();
        a.setStudentNo(studentNo); a.setCourseId(courseId);
        a.setSessionDate(date); a.setStatus(status); a.setNote(note);
        attendanceDao.insert(a);
    }

    public void recordBulk(List<String> studentNos, Long courseId, LocalDate date, List<String> statuses) {
        for (int i = 0; i < studentNos.size(); i++) {
            String status = (i < statuses.size()) ? statuses.get(i) : "PRESENT";
            record(studentNos.get(i), courseId, date, status, null);
        }
    }

    public Map<String,Object> getSummary(String studentNo, Long courseId) {
        return attendanceDao.getAttendanceSummary(studentNo, courseId);
    }
}
