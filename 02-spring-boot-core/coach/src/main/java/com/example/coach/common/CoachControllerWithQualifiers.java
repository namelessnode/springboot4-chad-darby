package com.example.coach.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoachControllerWithQualifiers {

    private final Coach coach;

    public CoachControllerWithQualifiers(@Qualifier("cricketCoach") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/dailyWorkoutsWithQualifier")
    public String getDailyWorkoutsWithQualifier() {
        return coach.dailyWorkout();
    }

}
