package com.example.coach.common;

import org.springframework.stereotype.Component;

@Component
public class TrackCoach implements Coach {
    @Override
    public String dailyWorkout() {
        return "Track Coach - Do track things";
    }
}
