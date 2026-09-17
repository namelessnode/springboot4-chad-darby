package com.example.coach.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeanLifeCycleController {

    Logger logger = LoggerFactory.getLogger(BeanLifeCycleController.class);

    private final Coach coach;

    public BeanLifeCycleController(Coach coach) {
        this.coach = coach;
    }

    @GetMapping("/withLifecycle")
    public String withLifecycle() {
        return coach.dailyWorkout();
    }

    @PostConstruct
    public void init() {
        logger.info("Constructor init");
    }

    @PreDestroy
    public void destroy() {
        logger.info("Constructor destroy");
    }
}
