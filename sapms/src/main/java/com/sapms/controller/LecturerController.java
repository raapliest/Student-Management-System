package com.sapms.controller;
import com.sapms.dao.*;
import com.sapms.service.*;
import com.sapms.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Controller
@RequestMapping("/lecturer")
public class LecturerController {
    @Autowired private LecturerDao lecturerDao;
    @Autowired private CourseDao courseDao;
    @Autowired private StudentDao studentDao;
    @Autowired private GradeService gradeService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private AnnouncementService announcementService;
    @Autowired private ProfileService profileService;
    @Autowired private JdbcTemplate jdbc;

    private Lecturer getLecturer(Authentication auth) {
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, auth.getName());
        return lecturerDao.findByUserId(userId);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Lecturer l = getLecturer(auth);
        List<Course> courses = courseDao.findByLecturer(l.getId());
        model.addAttribute("lecturer", l);
        model.addAttribute("courses", courses);
        model.addAttribute("announcements", announcementService.getForRole("LECTURER"));
        int totalStudents = 0;
        for (Course c : courses) {
            totalStudents += jdbc.queryForObject("SELECT COUNT(DISTINCT student_no) FROM grade WHERE course_id=?", Integer.class, c.getId());
        }
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalCourses", courses.size());
        return "lecturer/dashboard";
    }

    @GetMapping("/courses/{courseId}/grades")
    public String grades(@PathVariable Long courseId, Model model) {
        Course course = courseDao.findById(courseId);
        List<Grade> grades = gradeService.getGradesByCourse(courseId);
        long passing = grades.stream().filter(g -> "PASS".equals(g.getStatus())).count();
        long failing = grades.stream().filter(g -> "FAIL".equals(g.getStatus())).count();
        long atRisk  = grades.stream().filter(g -> "AT_RISK".equals(g.getStatus())).count();
        model.addAttribute("course", course);
        model.addAttribute("grades", grades);
        model.addAttribute("students", studentDao.findAll());
        model.addAttribute("passingCount", passing);
        model.addAttribute("failingCount", failing);
        model.addAttribute("atRiskCount", atRisk);
        return "lecturer/grades";
    }

    @PostMapping("/courses/{courseId}/grades/save")
    public String saveGrade(@PathVariable Long courseId, @RequestParam String studentNo,
                             @RequestParam(required=false) BigDecimal assignment,
                             @RequestParam(required=false) BigDecimal midterm,
                             @RequestParam(required=false) BigDecimal finalScore) {
        gradeService.saveGrade(studentNo, courseId, assignment, midterm, finalScore);
        return "redirect:/lecturer/courses/" + courseId + "/grades";
    }

    @GetMapping("/courses/{courseId}/attendance")
    public String attendance(@PathVariable Long courseId, Model model) {
        Course course = courseDao.findById(courseId);
        List<Attendance> records = attendanceService.getByCourse(courseId);
        List<Student> students = studentDao.findAll();
        long presentCount = records.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
        long absentCount  = records.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
        long lateCount    = records.stream().filter(a -> "LATE".equals(a.getStatus())).count();
        model.addAttribute("course", course);
        model.addAttribute("attendanceRecords", records);
        model.addAttribute("students", students);
        model.addAttribute("today", LocalDate.now().toString());
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("lateCount", lateCount);
        return "lecturer/attendance";
    }

    @PostMapping("/courses/{courseId}/attendance/record")
    public String recordAttendance(@PathVariable Long courseId, @RequestParam String studentNo,
                                    @RequestParam String sessionDate, @RequestParam String status,
                                    @RequestParam(required=false) String note) {
        attendanceService.record(studentNo, courseId, LocalDate.parse(sessionDate), status, note);
        return "redirect:/lecturer/courses/" + courseId + "/attendance";
    }

    @PostMapping("/courses/{courseId}/attendance/bulk")
    public String recordBulkAttendance(@PathVariable Long courseId,
                                        @RequestParam String sessionDate,
                                        @RequestParam List<String> studentNos,
                                        @RequestParam List<String> statuses) {
        attendanceService.recordBulk(studentNos, courseId, LocalDate.parse(sessionDate), statuses);
        return "redirect:/lecturer/courses/" + courseId + "/attendance?bulk=success";
    }

    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        Lecturer l = getLecturer(auth);
        List<Course> courses = courseDao.findByLecturer(l.getId());
        model.addAttribute("lecturer", l);
        model.addAttribute("courses", courses);
        List<java.util.Map<String,Object>> stats = new java.util.ArrayList<>();
        for (Course c : courses) {
            java.util.Map<String,Object> row = jdbc.queryForMap(
                "SELECT COUNT(*) as total,SUM(CASE WHEN status='PASS' THEN 1 ELSE 0 END) as passing," +
                "SUM(CASE WHEN status='FAIL' THEN 1 ELSE 0 END) as failing," +
                "SUM(CASE WHEN status='AT_RISK' THEN 1 ELSE 0 END) as at_risk," +
                "ROUND(AVG(total_score),2) as avg_score FROM grade WHERE course_id=?", c.getId());
            row.put("courseCode", c.getCourseCode());
            row.put("courseName", c.getCourseName());
            stats.add(row);
        }
        model.addAttribute("stats", stats);
        return "lecturer/reports";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Lecturer l = getLecturer(auth);
        model.addAttribute("lecturer", l);
        model.addAttribute("username", auth.getName());
        return "lecturer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth, @RequestParam String fullName, @RequestParam String email) {
        Lecturer l = getLecturer(auth);
        profileService.updateLecturerProfile(l.getId(), fullName, email);
        return "redirect:/lecturer/profile?success";
    }

    @PostMapping("/profile/password")
    public String changePassword(Authentication auth, @RequestParam String oldPassword,
                                  @RequestParam String newPassword, Model model) {
        boolean ok = profileService.changePassword(auth.getName(), oldPassword, newPassword);
        Lecturer l = getLecturer(auth);
        model.addAttribute("lecturer", l);
        model.addAttribute("username", auth.getName());
        if (ok) model.addAttribute("pwSuccess", true);
        else model.addAttribute("pwError", "Old password is incorrect.");
        return "lecturer/profile";
    }
}
