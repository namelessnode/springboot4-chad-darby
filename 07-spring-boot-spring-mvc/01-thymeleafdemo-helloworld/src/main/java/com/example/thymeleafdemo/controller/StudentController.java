package com.example.thymeleafdemo.controller;

import com.example.thymeleafdemo.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StudentController {

    @Value(("${countries}"))
    List<String> countries;
    @Value("${languages}")
    List<String> languages;
    @Value("${systems}")
    List<String> systems;

    @GetMapping("/studentForm")
    public String getForm(Model model){
        model.addAttribute("student", new Student());
        model.addAttribute("countries", countries);
        model.addAttribute("languages", languages);
        model.addAttribute("systems", systems);
        return "student-form";
    }

    @PostMapping("/processStudentForm")
    public String processStudentForm(@ModelAttribute("student") Student student){
        System.out.println("first name = "+student.getFirstName() + ", lastName = "+student.getLastName() + ", country = "+ student.getCountry() + ", favourite language = " +student.getFavouriteLanguage() + ", favourite systems = " + student.getFavouriteSystems());
        return "show-confirmation";
    }
}
