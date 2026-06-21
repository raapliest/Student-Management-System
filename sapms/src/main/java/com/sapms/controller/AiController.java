package com.sapms.controller;

import com.sapms.dao.AttendanceDao;
import com.sapms.dao.CourseDao;
import com.sapms.dao.GradeDao;
import com.sapms.dao.StudentDao;
import com.sapms.entity.Grade;
import com.sapms.entity.Attendance;
import com.sapms.entity.Student;
import com.sapms.pattern.factory.AiPrompt;
import com.sapms.pattern.factory.AiPromptFactory;
import com.sapms.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.sapms.util.MarkdownUtil;

import java.util.stream.Collectors;

@Controller
public class AiController {

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private GradeDao gradeDao;
    @Autowired
    private AttendanceDao attendanceDao;
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private AiService aiService;

    // ── Student AI Advisor ──
    @GetMapping("/student/ai-advisor")
    public String studentAdvisor(Authentication auth, Model model) {
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, auth.getName());
        Student s = studentDao.findByUserId(userId);
        model.addAttribute("student", s);
        model.addAttribute("aiResponse", "");
        return "student/ai-advisor";
    }

    @PostMapping("/student/ai-advisor")
    public String studentAdvisorAnalyze(Authentication auth, Model model) {
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, auth.getName());
        Student s = studentDao.findByUserId(userId);

        List<Grade> grades = gradeDao.findByStudent(s.getStudentNo());
        List<Attendance> attendance = attendanceDao.findByStudent(s.getStudentNo());

        String gradesData = grades.isEmpty() ? "No grades yet." :
                grades.stream().map(g -> String.format("  - %s: Assignment=%.1f, Midterm=%.1f, Final=%.1f, Total=%.1f (%s) [%s]",
                        g.getCourseCode(),
                        g.getAssignmentScore() != null ? g.getAssignmentScore().doubleValue() : 0,
                        g.getMidtermScore() != null ? g.getMidtermScore().doubleValue() : 0,
                        g.getFinalScore() != null ? g.getFinalScore().doubleValue() : 0,
                        g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0,
                        g.getGradeLetter() != null ? g.getGradeLetter() : "-",
                        g.getStatus()
                )).collect(Collectors.joining("\n"));

        long present = attendance.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
        long absent = attendance.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
        long late = attendance.stream().filter(a -> "LATE".equals(a.getStatus())).count();
        double rate = attendance.isEmpty() ? 0 : Math.round((double) present / attendance.size() * 100 * 10) / 10.0;
        String attendanceData = String.format("Present: %d, Absent: %d, Late: %d, Attendance Rate: %.1f%%", present, absent, late, rate);

        // DESIGN PATTERN: Factory Method
        // AiPromptFactory hands back the correct AiPrompt product for this role.
        // This controller never imports StudentAdvisorPrompt directly — it only
        // knows the AiPrompt interface and the Role enum.
        AiPrompt prompt = AiPromptFactory.create(AiPromptFactory.Role.STUDENT);
        String combinedData = "Student: " + s.getFullName()
                + "\n\nGrade Data:\n" + gradesData
                + "\n\nAttendance Data:\n" + attendanceData;
        String aiResponse = aiService.chat(prompt.buildSystemPrompt(), prompt.buildUserPrompt(combinedData));

        model.addAttribute("student", s);
        model.addAttribute("aiResponse", MarkdownUtil.toHtml(aiResponse));
        return "student/ai-advisor";
    }

    private boolean isLecturerCourse(Authentication auth, Long courseId) {
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, auth.getName());
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM course c JOIN lecturer l ON c.lecturer_id=l.id WHERE c.id=? AND l.user_id=?",
                Integer.class, courseId, userId);
        return count != null && count > 0;
    }

    // ── Lecturer Early Warning ──
    @GetMapping("/lecturer/ai-warning/{courseId}")
    public String lecturerWarning(@PathVariable Long courseId, Authentication auth, Model model) {
        if (!isLecturerCourse(auth, courseId)) return "redirect:/lecturer/dashboard";
        model.addAttribute("course", courseDao.findById(courseId));
        model.addAttribute("aiResponse", "");
        return "lecturer/ai-warning";
    }

    @PostMapping("/lecturer/ai-warning/{courseId}")
    public String lecturerWarningAnalyze(@PathVariable Long courseId, Authentication auth, Model model) {
        if (!isLecturerCourse(auth, courseId)) return "redirect:/lecturer/dashboard";
        var course = courseDao.findById(courseId);
        List<Grade> grades = gradeDao.findByCourse(courseId);

        String studentsData = grades.isEmpty() ? "No grade data available." :
                grades.stream().map(g -> String.format("  - %s (%s): Total=%.1f, Grade=%s, Status=%s",
                        g.getStudentName(), g.getStudentNo(),
                        g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0,
                        g.getGradeLetter() != null ? g.getGradeLetter() : "-",
                        g.getStatus()
                )).collect(Collectors.joining("\n"));

        // DESIGN PATTERN: Factory Method — LECTURER role -> LecturerWarningPrompt
        AiPrompt prompt = AiPromptFactory.create(AiPromptFactory.Role.LECTURER);
        String combinedData = "Course: " + course.getCourseName() + "\n\n" + studentsData;
        String aiResponse = aiService.chat(prompt.buildSystemPrompt(), prompt.buildUserPrompt(combinedData));

        model.addAttribute("course", course);
        model.addAttribute("aiResponse", MarkdownUtil.toHtml(aiResponse));
        return "lecturer/ai-warning";
    }

    // ── Admin Report Summarizer ──
    @GetMapping("/admin/ai-report")
    public String adminAiReport(Model model) {
        model.addAttribute("aiResponse", "");
        return "admin/ai-report";
    }

    @PostMapping("/admin/ai-report")
    public String adminAiReportAnalyze(Model model) {
        List<java.util.Map<String, Object>> gradeStats = jdbc.queryForList(
                "SELECT c.course_name, c.course_code, COUNT(g.id) as total, " +
                        "SUM(CASE WHEN g.status='PASS' THEN 1 ELSE 0 END) as passing, " +
                        "SUM(CASE WHEN g.status='FAIL' THEN 1 ELSE 0 END) as failing, " +
                        "SUM(CASE WHEN g.status='AT_RISK' THEN 1 ELSE 0 END) as at_risk, " +
                        "ROUND(AVG(g.total_score),2) as avg_score " +
                        "FROM course c LEFT JOIN grade g ON c.id=g.course_id GROUP BY c.id");

        int totalStudents = jdbc.queryForObject("SELECT COUNT(*) FROM student", Integer.class);
        int totalPass = jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='PASS'", Integer.class);
        int totalFail = jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='FAIL'", Integer.class);
        int totalRisk = jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='AT_RISK'", Integer.class);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total Students: %d\nPassing: %d, Failing: %d, At Risk: %d\n\nCourse Breakdown:\n",
                totalStudents, totalPass, totalFail, totalRisk));
        for (var row : gradeStats) {
            sb.append(String.format("- %s (%s): %s students, Pass=%s, Fail=%s, AtRisk=%s, Avg=%.1f\n",
                    row.get("course_name"), row.get("course_code"),
                    row.get("total"), row.get("passing"), row.get("failing"), row.get("at_risk"),
                    row.get("avg_score") != null ? Double.parseDouble(row.get("avg_score").toString()) : 0.0
            ));
        }

        // DESIGN PATTERN: Factory Method — ADMIN role -> AdminReportPrompt
        AiPrompt prompt = AiPromptFactory.create(AiPromptFactory.Role.ADMIN);
        String aiResponse = aiService.chat(prompt.buildSystemPrompt(), prompt.buildUserPrompt(sb.toString()));

        model.addAttribute("aiResponse", MarkdownUtil.toHtml(aiResponse));
        return "admin/ai-report";
    }
}
