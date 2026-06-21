package com.sapms.controller;

import com.sapms.dao.*;
import com.sapms.service.*;
import com.sapms.entity.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private GpaService gpaService;
    @Autowired
    private EnrollmentDao enrollmentDao;
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private JdbcTemplate jdbc;

    private Student getStudent(Authentication auth) {
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, auth.getName());
        return studentDao.findByUserId(userId);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        List<Grade> grades = gradeService.getGradesByStudent(s.getStudentNo());
        List<Attendance> attendance = attendanceService.getByStudent(s.getStudentNo());
        long passing = grades.stream().filter(g -> "PASS".equals(g.getStatus())).count();
        long failing = grades.stream().filter(g -> "FAIL".equals(g.getStatus())).count();
        long present = attendance.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
        double attRate = attendance.isEmpty() ? 0 : Math.round((double) present / attendance.size() * 100 * 10) / 10.0;
        var gpaData = gpaService.getStudentGpa(s.getStudentNo());
        model.addAttribute("student", s);
        model.addAttribute("grades", grades);
        model.addAttribute("attendance", attendance);
        model.addAttribute("passingCount", passing);
        model.addAttribute("failingCount", failing);
        model.addAttribute("attendanceRate", attRate);
        model.addAttribute("gpa", gpaData.get("gpa"));
        model.addAttribute("gpaLabel", gpaData.get("gpaLabel"));
        model.addAttribute("announcements", announcementService.getForRole("STUDENT"));
        return "student/dashboard";
    }

    @GetMapping("/grades")
    public String grades(Authentication auth, Model model) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        List<Grade> grades = gradeService.getGradesByStudent(s.getStudentNo());
        double avg = grades.stream().filter(g -> g.getTotalScore() != null).mapToDouble(g -> g.getTotalScore().doubleValue()).average().orElse(0);
        var gpaData = gpaService.getStudentGpa(s.getStudentNo());
        model.addAttribute("student", s);
        model.addAttribute("grades", grades);
        model.addAttribute("avgScore", Math.round(avg * 100) / 100.0);
        model.addAttribute("gpa", gpaData.get("gpa"));
        model.addAttribute("gpaLabel", gpaData.get("gpaLabel"));
        model.addAttribute("totalCredits", gpaData.get("totalCredits"));
        return "student/grades";
    }

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        List<Attendance> records = attendanceService.getByStudent(s.getStudentNo());
        long present = records.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
        long absent = records.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
        long late = records.stream().filter(a -> "LATE".equals(a.getStatus())).count();
        double rate = records.isEmpty() ? 0 : Math.round((double) present / records.size() * 100 * 10) / 10.0;
        model.addAttribute("student", s);
        model.addAttribute("records", records);
        model.addAttribute("presentCount", present);
        model.addAttribute("absentCount", absent);
        model.addAttribute("lateCount", late);
        model.addAttribute("attendanceRate", rate);
        return "student/attendance";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        model.addAttribute("student", s);
        model.addAttribute("username", auth.getName());
        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth, @RequestParam String fullName, @RequestParam String email) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        profileService.updateStudentProfile(s.getStudentNo(), fullName, email);
        return "redirect:/student/profile?success";
    }

    @GetMapping("/enrollment")
    public String enrollment(Authentication auth, Model model) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        Long activeYearId;
        try {
            activeYearId = jdbc.queryForObject("SELECT id FROM academic_year WHERE is_active=1 LIMIT 1", Long.class);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            activeYearId = s.getAcademicYearId();
        }
        String activeYearLabel = jdbc.queryForObject("SELECT year_label FROM academic_year WHERE id=?", String.class, activeYearId);
        List<Course> enrolledCourses = courseDao.findEnrolledByStudent(s.getStudentNo());
        List<Long> enrolledIds = enrolledCourses.stream().map(Course::getId).collect(java.util.stream.Collectors.toList());
        List<Course> available = courseDao.findByMajorAndYear(s.getMajorNo(), activeYearId).stream().filter(c -> !enrolledIds.contains(c.getId())).collect(java.util.stream.Collectors.toList());
        model.addAttribute("student", s);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("availableCourses", available);
        model.addAttribute("enrolledIds", enrolledIds);
        model.addAttribute("activeYearLabel", activeYearLabel);
        return "student/enrollment";
    }

    @PostMapping("/enrollment/enroll")
    public String enroll(Authentication auth, @RequestParam Long courseId) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        enrollmentDao.enroll(s.getStudentNo(), courseId);
        return "redirect:/student/enrollment?enrolled";
    }

    @PostMapping("/enrollment/unenroll")
    public String unenroll(Authentication auth, @RequestParam Long courseId) {
        Student s = getStudent(auth);
        if (s == null) return "redirect:/login";
        enrollmentDao.unenroll(s.getStudentNo(), courseId);
        return "redirect:/student/enrollment?unenrolled";
    }

    @PostMapping("/profile/password")
    public String changePassword(Authentication auth, @RequestParam String oldPassword, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        boolean ok = profileService.changePassword(auth.getName(), oldPassword, newPassword);
        if (ok) redirectAttributes.addFlashAttribute("pwSuccess", true);
        else redirectAttributes.addFlashAttribute("pwError", "Old password is incorrect.");
        return "redirect:/student/profile";
    }
}
