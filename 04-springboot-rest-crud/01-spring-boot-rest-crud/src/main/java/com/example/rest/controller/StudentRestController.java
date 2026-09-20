package com.example.rest.controller;

import com.example.rest.entity.Student;
import com.example.rest.entity.StudentErrorResponse;
import com.example.rest.entity.StudentNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    List<Student> students = new ArrayList<>();

    @PostConstruct
    public void loadData(){
        students.add(new Student("firstName A", "lastName A"));
        students.add(new Student("firstName B", "lastName B"));
        students.add(new Student("firstName C", "lastName C"));
    }

    @GetMapping("/students")
    public List<Student> getStudents() {
        return students;
    }

    @GetMapping("/students/{studentId}")
    public Student getStudent(@PathVariable int studentId){

        if(studentId < 0 || studentId >= students.size())
            throw new StudentNotFoundException("Student not found with id " + studentId);
        return students.get(studentId);
    }
}
