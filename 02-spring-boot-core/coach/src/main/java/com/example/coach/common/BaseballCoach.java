package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class BaseballCoach implements Coach {

    Logger logger = LoggerFactory.getLogger(BaseballCoach.class);

    public BaseballCoach() {
        logger.info("BaseballCoach constructor called");
    }

    @Override
    public String dailyWorkout() {
        return "Baseball Coach - Do baseball things";
    }
}
