package com.example.thymeleafdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class DemoController {

    @GetMapping("/helloworld")
    public String helloworld(Model model){
        model.addAttribute("date", LocalDateTime.now());
        return "helloworld";
    }
}
