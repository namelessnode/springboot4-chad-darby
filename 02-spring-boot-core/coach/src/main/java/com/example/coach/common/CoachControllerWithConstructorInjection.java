package com.example.coach.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Lazy
//When lazy initialized at class level both the bean and controller are called when endpoint is hit
public class CoachControllerWithConstructorInjection {

    Logger logger = LoggerFactory.getLogger(CoachControllerWithConstructorInjection.class);

    private final Coach coach;

    //    Autowired only required when more than one constructors are present.
    @Autowired
    public CoachControllerWithConstructorInjection(
            @Qualifier("baseballCoach") @Lazy Coach coach) {
        logger.info("CoachControllerWithConstructorInjection constructor called");
        this.coach = coach;
    }
//    When @lazy is inside the method parameter then controller constructor will be called at startup
//    but the logs for the bean i.e. the actual bean creation will happen only once the endpoint is hit

    @GetMapping("/dailyWorkoutWithConstructor")
    public String getDailyWorkout() {
        return coach.dailyWorkout();
    }
}
