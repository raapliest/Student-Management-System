package com.sapms.controller;
import com.sapms.dao.MajorInfoDao;
import com.sapms.entity.Student;
import com.sapms.service.StudentRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class RegisterController {
    @Autowired private MajorInfoDao majorInfoDao;
    @Autowired private StudentRegisterService registerService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("majors", majorInfoDao.findAll());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName, @RequestParam String email,
                           @RequestParam String majorNo, Model model) {
        try {
            Student student = registerService.register(fullName, email, majorNo);
            model.addAttribute("student", student);
            return "auth/register-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("majors", majorInfoDao.findAll());
            return "auth/register";
        }
    }
}