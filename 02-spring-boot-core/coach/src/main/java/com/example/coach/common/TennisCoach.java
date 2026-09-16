package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TennisCoach implements Coach {

    Logger logger = LoggerFactory.getLogger(TennisCoach.class);

    public TennisCoach() {
        logger.info("TennisCoach constructor called");
    }

    @Override
    public String dailyWorkout() {
        return "Tennis Coach - Do Tennis things";
    }
}
