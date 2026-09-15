package com.example.coach.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoachControllerWithConstructorInjection {

    private final Coach coach;

    public CoachControllerWithConstructorInjection(
            @Qualifier("baseballCoach") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/dailyWorkoutWithConstructor")
    public String getDailyWorkout() {
        return coach.dailyWorkout();
    }
}
