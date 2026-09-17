package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwimCoach implements Coach{

    Logger logger =  LoggerFactory.getLogger(SwimCoach.class);

    public SwimCoach() {
        logger.info("Swim Coach constructor");
    }

    @Override
    public String dailyWorkout() {
        return "Swim Coach: Do swimming things";
    }
}
