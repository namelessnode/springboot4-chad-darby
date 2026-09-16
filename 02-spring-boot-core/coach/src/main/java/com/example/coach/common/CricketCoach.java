package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CricketCoach implements Coach {

    Logger logger = LoggerFactory.getLogger(CricketCoach.class);

    public CricketCoach() {
        logger.info("CricketCoach constructor called");
    }

    @Override
    public String dailyWorkout() {
        return "Cricket Coach - Practice bowling";
    }
}
