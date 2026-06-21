package com.sapms.controller;

import com.sapms.dao.*;
import com.sapms.entity.*;
import com.sapms.service.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private LecturerDao lecturerDao;
    @Autowired
    private MajorInfoDao majorInfoDao;
    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private AcademicYearDao academicYearDao;
    @Autowired
    private StudentRegisterService registerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentDao.countAll());
        model.addAttribute("totalCourses", courseDao.countAll());
        model.addAttribute("totalLecturers", lecturerDao.countAll());
        model.addAttribute("passingCount", jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='PASS'", Integer.class));
        model.addAttribute("failingCount", jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='FAIL'", Integer.class));
        model.addAttribute("atRiskCount", jdbc.queryForObject("SELECT COUNT(*) FROM grade WHERE status='AT_RISK'", Integer.class));
        model.addAttribute("announcements", announcementService.getAll());
        model.addAttribute("recentStudents", studentDao.findAll());
        return "admin/dashboard";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String fullName, @RequestParam String email, @RequestParam String majorNo, RedirectAttributes redirectAttributes) {
        try {
            Student s = registerService.register(fullName, email, majorNo);
            redirectAttributes.addFlashAttribute("addedNo", s.getStudentNo());
            redirectAttributes.addFlashAttribute("addedName", s.getFullName());
            return "redirect:/admin/students?added";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addError", e.getMessage());
            return "redirect:/admin/students?addError";
        }
    }

    @GetMapping("/students")
    public String students(@RequestParam(required = false) String q, @RequestParam(required = false) String majorNo, Model model) {
        model.addAttribute("students", studentDao.search(q, majorNo));
        model.addAttribute("majors", majorInfoDao.findAll());
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("selectedMajor", majorNo != null ? majorNo : "");
        return "admin/students";
    }

    @PostMapping("/students/edit/{studentNo}")
    public String editStudent(@PathVariable String studentNo, @RequestParam String fullName, @RequestParam String email) {
        studentDao.update(studentNo, fullName, email);
        return "redirect:/admin/students?success";
    }

    @PostMapping("/students/delete/{studentNo}")
    public String deleteStudent(@PathVariable String studentNo) {
        studentDao.delete(studentNo);
        return "redirect:/admin/students?deleted";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseDao.findAll());
        model.addAttribute("majors", majorInfoDao.findAll());
        model.addAttribute("lecturers", lecturerDao.findAll());
        try {
            String label = jdbc.queryForObject("SELECT year_label FROM academic_year WHERE is_active=1 LIMIT 1", String.class);
            model.addAttribute("activeYearLabel", label);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            model.addAttribute("activeYearLabel", null);
        }
        return "admin/courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String courseCode, @RequestParam String courseName, @RequestParam int creditHours, @RequestParam String majorNo, @RequestParam Long lecturerId) {
        Long activeYearId;
        try {
            activeYearId = jdbc.queryForObject("SELECT id FROM academic_year WHERE is_active=1 LIMIT 1", Long.class);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return "redirect:/admin/courses?noActiveYear";
        }
        Course c = new Course();
        c.setCourseCode(courseCode);
        c.setCourseName(courseName);
        c.setCreditHours(creditHours);
        c.setMajorNo(majorNo);
        c.setLecturerId(lecturerId);
        c.setAcademicYearId(activeYearId);
        courseDao.insert(c);
        return "redirect:/admin/courses?success";
    }

    @PostMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable Long id, @RequestParam String courseCode, @RequestParam String courseName, @RequestParam int creditHours, @RequestParam String majorNo, @RequestParam Long lecturerId) {
        Course c = new Course();
        c.setId(id);
        c.setCourseCode(courseCode);
        c.setCourseName(courseName);
        c.setCreditHours(creditHours);
        c.setMajorNo(majorNo);
        c.setLecturerId(lecturerId);
        courseDao.update(c);
        return "redirect:/admin/courses?success";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseDao.delete(id);
        return "redirect:/admin/courses?deleted";
    }

    @GetMapping("/lecturers")
    public String lecturers(Model model) {
        model.addAttribute("lecturers", lecturerDao.findAll());
        return "admin/lecturers";
    }

    @PostMapping("/lecturers/add")
    public String addLecturer(@RequestParam String username, @RequestParam String fullName, @RequestParam String email, @RequestParam String lecturerNo) {
        String encodedPw = passwordEncoder.encode("password");
        jdbc.update("INSERT INTO users(username,password,role) VALUES(?,?,'LECTURER')", username, encodedPw);
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, username);
        Lecturer l = new Lecturer();
        l.setUserId(userId);
        l.setLecturerNo(lecturerNo);
        l.setFullName(fullName);
        l.setEmail(email);
        lecturerDao.insert(l);
        return "redirect:/admin/lecturers?success";
    }

    @PostMapping("/lecturers/edit/{id}")
    public String editLecturer(@PathVariable Long id, @RequestParam String fullName, @RequestParam String email) {
        Lecturer l = new Lecturer();
        l.setId(id);
        l.setFullName(fullName);
        l.setEmail(email);
        lecturerDao.update(l);
        return "redirect:/admin/lecturers?success";
    }

    @PostMapping("/lecturers/delete/{id}")
    public String deleteLecturer(@PathVariable Long id) {
        lecturerDao.delete(id);
        return "redirect:/admin/lecturers?deleted";
    }

    @GetMapping("/announcements")
    public String announcements(Model model) {
        model.addAttribute("announcements", announcementService.getAll());
        return "admin/announcements";
    }

    @PostMapping("/announcements/add")
    public String addAnnouncement(@RequestParam String title, @RequestParam String content, @RequestParam String targetRole, Authentication auth) {
        announcementService.post(title, content, auth.getName(), targetRole);
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return "redirect:/admin/announcements";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("gradeStats", jdbc.queryForList("SELECT c.course_name,c.course_code,COUNT(g.id) as total," + "SUM(CASE WHEN g.status='PASS' THEN 1 ELSE 0 END) as passing," + "SUM(CASE WHEN g.status='FAIL' THEN 1 ELSE 0 END) as failing," + "SUM(CASE WHEN g.status='AT_RISK' THEN 1 ELSE 0 END) as at_risk," + "ROUND(AVG(g.total_score),2) as avg_score FROM course c LEFT JOIN grade g ON c.id=g.course_id GROUP BY c.id ORDER BY c.course_code"));
        model.addAttribute("attendanceStats", jdbc.queryForList("SELECT c.course_name,c.course_code,COUNT(a.id) as total_records," + "SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END) as present_count," + "SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END) as absent_count," + "SUM(CASE WHEN a.status='LATE' THEN 1 ELSE 0 END) as late_count " + "FROM course c LEFT JOIN attendance a ON c.id=a.course_id GROUP BY c.id ORDER BY c.course_code"));
        return "admin/reports";
    }

    @GetMapping("/academic-years")
    public String academicYears(Model model) {
        model.addAttribute("years", academicYearDao.findAll());
        return "admin/academic-years";
    }

    @PostMapping("/academic-years/add")
    public String addYear(@RequestParam String startYear, @RequestParam(required = false) String endYear) {
        academicYearDao.insert(startYear, endYear);
        return "redirect:/admin/academic-years?success";
    }

    @PostMapping("/academic-years/activate/{id}")
    public String activateYear(@PathVariable Long id) {
        academicYearDao.setActive(id);
        return "redirect:/admin/academic-years?activated";
    }

    @PostMapping("/academic-years/delete/{id}")
    public String deleteYear(@PathVariable Long id) {
        boolean ok = academicYearDao.delete(id);
        if (ok) return "redirect:/admin/academic-years?deleted";
        return "redirect:/admin/academic-years?deleteError";
    }
}
