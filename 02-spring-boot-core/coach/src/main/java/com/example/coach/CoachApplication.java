package com.example.coach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.coach", "com.example.outsidebasepackage"})
public class CoachApplication {

    static void main(String[] args) {
        SpringApplication.run(CoachApplication.class, args);
    }

}
