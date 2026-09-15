package com.example.coach.common;

import org.springframework.stereotype.Component;

@Component
public class CricketCoach implements Coach {
    @Override
    public String dailyWorkout() {
        return "Cricket Coach - Practice bowling";
    }
}
