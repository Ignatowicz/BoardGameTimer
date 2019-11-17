package com.studio2.bgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class BoardGameTimerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardGameTimerApplication.class, args);
    }

}