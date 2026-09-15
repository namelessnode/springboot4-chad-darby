package com.example.coach.common;

import org.springframework.stereotype.Component;

@Component
public class TennisCoach implements Coach {
    @Override
    public String dailyWorkout() {
        return "Tennis Coach - Do Tennis things";
    }
}
