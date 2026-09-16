package com.example.coach.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoachControllerWithPrimaryAnnotation {

    private final Coach coach;

//    If qualifier is provided then tennis coach will take preference even though cricket coach is primary
    public CoachControllerWithPrimaryAnnotation(@Qualifier("tennisCoach") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/dailyWorkoutWithPrimaryAnnotation")
    public String  doWorkoutWithPrimaryAnnotation() {
        return coach.dailyWorkout();
    }
}
