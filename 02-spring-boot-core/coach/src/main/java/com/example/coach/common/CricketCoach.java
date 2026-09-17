package com.example.coach.common;

import org.apache.catalina.core.ApplicationFilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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
