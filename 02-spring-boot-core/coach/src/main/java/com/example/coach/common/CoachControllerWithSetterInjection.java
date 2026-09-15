package com.example.coach.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoachControllerWithSetterInjection {

    private Coach coach;

    @Autowired
    public void setCoach(@Qualifier("tennisCoach") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/dailyWorkoutWithSetter")
    public String dailyWorkoutWithSetter() {
        return coach.dailyWorkout();
    }
}
