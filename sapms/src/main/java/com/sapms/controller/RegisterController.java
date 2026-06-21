package com.sapms.controller;

import com.sapms.dao.MajorInfoDao;
import com.sapms.entity.Student;
import com.sapms.service.StudentRegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// Self-registration removed — student accounts are created by admin only.
@Controller
public class RegisterController {
    @RequestMapping({"/register", "/register/**"})
    public String disabled() {
        return "redirect:/login";
    }
}