package com.example.coach.common;

import org.springframework.stereotype.Component;

@Component
public class BaseballCoach implements Coach {
    @Override
    public String dailyWorkout() {
        return "Baseball Coach - Do baseball things";
    }
}
