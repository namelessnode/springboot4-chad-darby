package com.example.thymeleafdemo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloWorldController {

    @GetMapping("/showForm")
    public String showForm() {
        return "helloworld-form";
    }

    @PostMapping("/processForm")
    public String processForm() {
        return "helloworld";
    }

    @PostMapping("/processFormVersion2")
    public String processFormV2(HttpServletRequest request, Model model) {
        String transformedName = request.getParameter("studentName").toUpperCase();
        model.addAttribute("message", "Transformed = "+transformedName);
        return "helloworld";
    }

    @RequestMapping(value = "/processFormVersion3", method = RequestMethod.POST)
    public String processFormV3(@RequestParam("studentName") String name, Model model) {
        model.addAttribute("messageV3", "Transformed = "+name);
        return "helloworld";
    }
}
