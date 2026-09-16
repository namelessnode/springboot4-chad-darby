package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrackCoach implements Coach {

    Logger logger = LoggerFactory.getLogger(TrackCoach.class);

    public TrackCoach() {
        logger.info("TrackCoach constructor called");
    }

    @Override
    public String dailyWorkout() {
        return "Track Coach - Do track things";
    }
}
