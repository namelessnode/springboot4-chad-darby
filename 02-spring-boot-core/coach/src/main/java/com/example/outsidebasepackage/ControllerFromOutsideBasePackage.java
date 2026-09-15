package com.example.outsidebasepackage;

import com.example.coach.common.Coach;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerFromOutsideBasePackage {

    private final Coach coach;

    public ControllerFromOutsideBasePackage(
            @Qualifier("trackCoach") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/dailyWorkoutFromOutsideBasePackage")
    public String dailyWorkoutFromOutsideBasePackage() {
        return coach.dailyWorkout();
    }
}
