package com.example.springbootapp.myapp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyControllers {

    @Value("${coach.name}")
    private String coach;
    @Value("${player.name}")
    private String player;

    @GetMapping("/")
    public String getHome() {
        return "Hello World";
    }

    @GetMapping("/firstEndpoint")
    public String getFirstEndpoint() {
        return coach;
    }

    @GetMapping("/secondEndpoint")
    public String getSecondEndpoint() {
        return player;
    }
}
