package com.example.coach.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwimCoachController {

    private Coach coach;

    public SwimCoachController(@Qualifier("aqua") Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/doSwimming")
    public String doSwimming(){
        return coach.dailyWorkout();
    }
}
