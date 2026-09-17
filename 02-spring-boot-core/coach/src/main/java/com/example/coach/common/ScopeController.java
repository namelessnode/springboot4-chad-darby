package com.example.coach.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScopeController {

    private final Coach firstCoach;
    private final Coach secondCoach;

    public ScopeController(Coach firstCoach, Coach secondCoach) {
        this.firstCoach = firstCoach;
        this.secondCoach = secondCoach;
    }

    @GetMapping("/checkScope")
    public boolean checkScope() {
        return firstCoach == secondCoach;
    }
}
