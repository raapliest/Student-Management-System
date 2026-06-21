package com.sapms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
    @RequestMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
